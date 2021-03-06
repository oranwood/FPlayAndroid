//
// FPlayAndroid is distributed under the FreeBSD License
//
// Copyright (c) 2013, Carlos Rafael Gimenes das Neves
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.
//
// https://github.com/carlosrafaelgn/FPlayAndroid
//
package br.com.carlosrafaelgn.fplay.list;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Random;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import br.com.carlosrafaelgn.fplay.ActivityItemView;
import br.com.carlosrafaelgn.fplay.activity.MainHandler;
import br.com.carlosrafaelgn.fplay.playback.Player;
import br.com.carlosrafaelgn.fplay.ui.SongView;
import br.com.carlosrafaelgn.fplay.ui.UI;
import br.com.carlosrafaelgn.fplay.util.Serializer;

//
//SINCE ALL CALLS MADE BY Player ARE MADE ON THE MAIN THREAD, THERE IS NO
//NEED TO SYNCHRONIZE THE ACCESS TO THE ITEMS
//
public final class SongList extends BaseList<Song> implements FileFetcher.Listener {
	public static final int HOW_CURRENT = -4;
	public static final int HOW_PREVIOUS = -3;
	public static final int HOW_NEXT_MANUAL = -2;
	public static final int HOW_NEXT_AUTO = -1;
	private int adding, currentShuffledItemIndex, shuffledItemsAlreadyPlayed, indexOfPreviouslyDeletedCurrentShuffledItem;
	public boolean selecting, moving;
	private Song[] shuffledList;
	public ActivityItemView observerActivity;
	private static final SongList theSongList = new SongList();
	
	private SongList() {
		super(Song.class);
		this.adding = 0;
		this.indexOfPreviouslyDeletedCurrentShuffledItem = -1;
	}
	
	public static SongList getInstance() {
		return theSongList;
	}
	
	//--------------------------------------------------------------------------------------------
	//These six methods are the only ones that can be called from any thread (all the others must be called from the main thread)
	public void startDeserializing(final Context context, final String path, final boolean entireListBeingLoaded, final boolean append, final boolean play) {
		try {
			addingStarted();
			(new Thread("List Deserializer Thread") {
				private boolean done;
				private int current;
				private Song[] songs;
				private Throwable ex;
				@Override
				public void run() {
					if (done) {
						if (songs == null)
							songs = new Song[0];
						deserializationEnded(songs, current, entireListBeingLoaded, append, play, ex);
						songs = null;
						ex = null;
					} else {
						FileInputStream fs = null;
						try {
							fs = context.openFileInput((path == null) ? "_List" : path);
							current = Serializer.deserializeInt(fs);
							final int version = Serializer.deserializeInt(fs);
							final int count = Serializer.deserializeInt(fs);
							if (version == 0x0100 && count > 0) {
								songs = new Song[count];
								for (int i = 0; i < count; i++)
									songs[i] = Song.deserialize(fs);
							}
							done = true;
							addingEnded();
							MainHandler.postToMainThread(this);
						} catch (Throwable ex) {
							songs = null;
							done = true;
							if (!(ex instanceof FileNotFoundException))
								this.ex = ex;
							addingEnded();
							MainHandler.postToMainThread(this);
						} finally {
							try {
								if (fs != null)
									fs.close();
							} catch (Throwable ex) {
							}
						}
					}
				}
			}).start();
		} catch (Throwable ex) {
			addingEnded();
		}
	}
	
	public void addingStarted() {
		synchronized (sync) {
			adding++;
		}
	}
	
	public void addingEnded() {
		synchronized (sync) {
			adding = ((adding <= 1) ? 0 : (adding - 1));
		}
	}
	
	public boolean isAdding() {
		return (adding > 0);
	}
	
	@Override
	public void onFilesFetched(FileFetcher fetcher, final Throwable e) {
		if (e != null) {
			addingEnded();
			MainHandler.postToMainThread(new Runnable() {
				@Override
				public void run() {
					try {
						UI.toast(Player.getService(), e);
					} catch (Throwable ex) { }
				}
			});
		} else {
			addFiles(fetcher.files, -1, fetcher.count, fetcher.playAfterFetching, true);
		}
	}
	
	public void addFiles(FileSt[] files, final int position, int count, final boolean play, final boolean isAddingFolder) {
		if (files == null || files.length == 0 || count <= 0) {
			addingEnded();
			return;
		}
		if (count > files.length)
			count = files.length;
		final Song[] songs = new Song[count];
		int f = 0;
		if (songs != null) {
			final byte[][] tmpPtr = new byte[][] { new byte[256] };
			for (int i = 0; i < count; i++) {
				if (!files[i].isDirectory) {
					songs[f] = new Song(files[i], tmpPtr);
					f++;
				}
			}
		}
		addingEnded();
		final int songCount = f;
		MainHandler.postToMainThread(new Runnable() {
			@Override
			public void run() {
				if (play && isAddingFolder && Player.clearListWhenPlayingFolders)
					clear();
				final int p = getCount();
				add(songs, position, songCount);
				Player.setSelectionAfterAdding(p);
				if (play)
					Player.play(p);
			}
		});
	}
	
	//--------------------------------------------------------------------------------------------
	
	private FileOutputStream fullSerialization(Context context, String path) throws IOException {
		FileOutputStream fs = context.openFileOutput((path == null) ? "_List" : path, 0);
		Serializer.serializeInt(fs, current);
		Serializer.serializeInt(fs, 0x0100);
		Serializer.serializeInt(fs, count);
		for (int i = 0; i < count; i++)
			items[i].serialize(fs);
		return fs;
	}
	
	public boolean serialize(Context context, String path) {
		FileOutputStream fs = null;
		RandomAccessFile rf = null;
		try {
			if (modificationVersion > 1 || path != null) {
				fs = fullSerialization(context, path);
			} else {
				try {
					final File f = context.getFileStreamPath("_List");
					rf = new RandomAccessFile(f, "rw");
					rf.seek(0);
					final byte[] buf = new byte[4];
					Serializer.serializeInt(buf, 0, current);
					rf.write(buf);
				} catch (Throwable ex) {
					try {
						if (rf != null)
							rf.close();
					} catch (Throwable ex2) {
					}
					rf = null;
					fs = fullSerialization(context, path);
				}
			}
		} catch (Throwable ex) {
			return false;
		} finally {
			try {
				if (rf != null)
					rf.close();
			} catch (Throwable ex) {
			}
			try {
				if (fs != null)
					fs.close();
			} catch (Throwable ex) {
			}
		}
		if (path == null)
			modificationVersion = 1;
		return true;
	}
	
	private void deserializationEnded(Song[] songs, int current, boolean entireListBeingLoaded, boolean append, boolean play, Throwable ex) {
		final int originalCount = count;
		int positionToSelect = -1;
		if (ex == null) {
			if (entireListBeingLoaded) {
				if (current >= 0)
					positionToSelect = originalCount + current;
			} else if (songs.length > 0) {
				if (append) {
					positionToSelect = originalCount;
				} else {
					positionToSelect = 0;
					clear();
				}
			}
			if (songs.length == 0)
				modificationVersion++; //increment here, since add() does not increment when count is 0
			else
				add(songs, -1, songs.length);
			if (positionToSelect >= this.count) {
				positionToSelect = -1;
			} else if (positionToSelect >= 0) {
				if (entireListBeingLoaded || play) {
					this.current = positionToSelect;
					this.firstSel = positionToSelect;
					this.lastSel = positionToSelect;
					this.originalSel = positionToSelect;
				}
				if (listObserver != null)
					listObserver.centerItem(positionToSelect, false);
			}
		}
		Player.onSongListDeserialized((positionToSelect >= 0) ? items[positionToSelect] : null, ((play && positionToSelect >= 0) ? positionToSelect : -1), positionToSelect, ex);
	}
	
	private Song getRandomSongAndSetCurrent(int how) {
		if (shuffledItemsAlreadyPlayed >= count)
			setRandomMode(true);
		int i;
		Song s = null;
		if (how == HOW_CURRENT) {
			how = ((indexOfPreviouslyDeletedCurrentShuffledItem >= 0) ? indexOfPreviouslyDeletedCurrentShuffledItem : currentShuffledItemIndex);
			if (how < 0) {
				how = current;
				if (how < 0 || how >= count) {
					how = 0;
				} else {
					s = items[how];
					for (i = count - 1; i >= 0; i--) {
						if (shuffledList[i] == s) {
							current = how;
							currentShuffledItemIndex = i;
							break;
						}
					}
				}
			} else if (how >= count) {
				how = count - 1;
			}
		} else if (how == HOW_PREVIOUS) {
			how = ((indexOfPreviouslyDeletedCurrentShuffledItem >= 0) ? indexOfPreviouslyDeletedCurrentShuffledItem : currentShuffledItemIndex) - 1;
			if (how < 0 || how >= count)
				how = count - 1;
		} else if (how == HOW_NEXT_AUTO || how == HOW_NEXT_MANUAL) {
			how = ((indexOfPreviouslyDeletedCurrentShuffledItem >= 0) ? indexOfPreviouslyDeletedCurrentShuffledItem : (currentShuffledItemIndex + 1));
			if (how < 0 || how >= count)
				how = 0;
		} else if (how >= 0 && how < count) {
			s = items[how];
			for (i = count - 1; i >= 0; i--) {
				if (shuffledList[i] == s) {
					current = how;
					currentShuffledItemIndex = i;
					break;
				}
			}
		}
		indexOfPreviouslyDeletedCurrentItem = -1;
		indexOfPreviouslyDeletedCurrentShuffledItem = -1;
		if (how < 0 || how >= count)
			return null;
		if (s == null) {
			s = shuffledList[how];
			for (i = count - 1; i >= 0; i--) {
				if (items[i] == s) {
					current = i;
					currentShuffledItemIndex = how;
					break;
				}
			}
		}
		if (!s.alreadyPlayed) {
			s.alreadyPlayed = true;
			shuffledItemsAlreadyPlayed++;
		}
		s.possibleNextSong = null;
		if (!selecting && !moving) {
			firstSel = current;
			lastSel = current;
			originalSel = current;
		}
		notifyDataSetChanged(-1, SELECTION_CHANGED);
		return s;
	}
	
	public Song getSongAndSetCurrent(int how) {
		if (shuffledList != null)
			return getRandomSongAndSetCurrent(how);
		if (how == HOW_CURRENT) {
			how = ((indexOfPreviouslyDeletedCurrentItem >= 0) ? indexOfPreviouslyDeletedCurrentItem : current);
			if (how < 0)
				how = 0;
			else if (how >= count)
				how = count - 1;
		} else if (how == HOW_PREVIOUS) {
			how = ((indexOfPreviouslyDeletedCurrentItem >= 0) ? indexOfPreviouslyDeletedCurrentItem : current) - 1;
			if (how < 0 || how >= count)
				how = count - 1;
		} else if (how == HOW_NEXT_AUTO || how == HOW_NEXT_MANUAL) {
			how = ((indexOfPreviouslyDeletedCurrentItem >= 0) ? indexOfPreviouslyDeletedCurrentItem : (current + 1));
			if (how < 0 || how >= count)
				how = 0;
		}
		indexOfPreviouslyDeletedCurrentItem = -1;
		indexOfPreviouslyDeletedCurrentShuffledItem = -1;
		if (how < 0 || how >= count)
			return null;
		current = how;
		final Song s = items[how];
		s.possibleNextSong = items[((how == (count - 1)) ? 0 : (how + 1))];
		if (!selecting && !moving) {
			firstSel = current;
			lastSel = current;
			originalSel = current;
		}
		notifyDataSetChanged(-1, SELECTION_CHANGED);
		return s;
	}
	
	private void setShuffledCapacity(int capacity) {
		if (capacity >= count) {
			if (shuffledList == null)
				shuffledList = new Song[capacity + LIST_DELTA];
			else if (capacity > shuffledList.length || capacity <= (shuffledList.length - (2 * LIST_DELTA)))
				shuffledList = Arrays.copyOf(shuffledList, capacity + LIST_DELTA);
		}
	}
	
	public boolean isInRandomMode() {
		return (shuffledList != null);
	}
	
	public void setRandomMode(boolean randomMode) {
		if ((shuffledList != null) == randomMode && !randomMode)
			return;
		int i;
		if (!randomMode) {
			if (shuffledList != null) {
				for (i = count - 1; i >= 0; i--)
					shuffledList[i] = null;
				shuffledList = null;
			}
		} else {
			setShuffledCapacity(count);
			for (i = count - 1; i >= 0; i--) {
				final Song s = items[i];
				s.alreadyPlayed = false;
				shuffledList[i] = s;
			}
			final Random r = new Random();
			for (i = (count - 1) >> 1; i >= 0; i--) {
				final int a = r.nextInt(count);
				final int b = r.nextInt(count);
				final Song s = shuffledList[a];
				shuffledList[a] = shuffledList[b];
				shuffledList[b] = s;
			}
		}
		currentShuffledItemIndex = -1;
		shuffledItemsAlreadyPlayed = 0;
	}
	
	@Override
	protected void addingItems(int position, int count) {
		if (shuffledList == null)
			return;
		setShuffledCapacity(this.count);
		final int initial = this.count - count;
		System.arraycopy(items, position, shuffledList, initial, count);
		final Random r = new Random();
		for (int i = initial + ((count - 1) >> 1); i >= initial; i--) {
			final int a = initial + r.nextInt(count);
			final int b = initial + r.nextInt(count);
			final Song s = shuffledList[a];
			shuffledList[a] = shuffledList[b];
			shuffledList[b] = s;
		}
	}
	
	@Override
	protected void removingItems(int position, int count) {
		if (shuffledList == null)
			return;
		int shuffledCount = this.count;
		count += position;
		while (position < count) {
			final Song s = items[position];
			for (int i = shuffledCount - 1; i >= 0; i--) {
				if (shuffledList[i] == s) {
					shuffledCount--;
					if (indexOfPreviouslyDeletedCurrentShuffledItem >= 0) {
						if (indexOfPreviouslyDeletedCurrentShuffledItem == i)
							indexOfPreviouslyDeletedCurrentShuffledItem = i;
						else if (indexOfPreviouslyDeletedCurrentShuffledItem > i)
							indexOfPreviouslyDeletedCurrentShuffledItem--;
						if (indexOfPreviouslyDeletedCurrentShuffledItem >= shuffledCount)
							indexOfPreviouslyDeletedCurrentShuffledItem = -1;
					}
					if (currentShuffledItemIndex == i) {
						indexOfPreviouslyDeletedCurrentShuffledItem = i;
						currentShuffledItemIndex = -1;
					} else if (currentShuffledItemIndex > i) {
						currentShuffledItemIndex--;
					}
					if (currentShuffledItemIndex >= shuffledCount)
						currentShuffledItemIndex = -1;
					System.arraycopy(shuffledList, i + 1, shuffledList, i, shuffledCount - i);
					shuffledList[shuffledCount] = null;
					if (s.alreadyPlayed)
						shuffledItemsAlreadyPlayed--;
					break;
				}
			}
			position++;
		}
	}
	
	@Override
	protected void clearingItems() {
		if (shuffledList == null)
			return;
		for (int i = count - 1; i >= 0; i--)
			shuffledList[i] = null;
		currentShuffledItemIndex = -1;
		shuffledItemsAlreadyPlayed = 0;
		indexOfPreviouslyDeletedCurrentShuffledItem = -1;
	}
	
	@Override
	protected void notifyDataSetChanged(int gotoPosition, int whatHappened) {
		super.notifyDataSetChanged(gotoPosition, whatHappened);
		if (whatHappened == LIST_CLEARED) {
			Player.listCleared();
		} else if (whatHappened != SELECTION_CHANGED && shuffledList == null && current >= 0 && current < count) {
			int n = current + 1;
			if (n < 0 || n >= count)
				n = 0;
			if (n < count)
				Player.nextMayHaveChanged(items[n]);
		}
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		final SongView view = ((convertView == null) ? new SongView(Player.getService(), observerActivity) : (SongView)convertView);
		view.setItemState(items[position], position, getItemState(position));
		return view;
	}
}
