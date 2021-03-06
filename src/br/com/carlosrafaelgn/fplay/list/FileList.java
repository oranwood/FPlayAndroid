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

import android.view.View;
import android.view.ViewGroup;
import br.com.carlosrafaelgn.fplay.ActivityFileView;
import br.com.carlosrafaelgn.fplay.playback.Player;
import br.com.carlosrafaelgn.fplay.ui.FileView;
import br.com.carlosrafaelgn.fplay.ui.UI;

//
//Supported Media Formats
//http://developer.android.com/guide/appendix/media-formats.html
//
public final class FileList extends BaseList<FileSt> implements FileFetcher.Listener {
	private String path, comingFrom;
	private FileFetcher fetcher;
	public ActivityFileView observerActivity;
	
	public FileList() {
		super(FileSt.class);
	}
	
	private void showNotification(boolean show) {
		if (observerActivity != null)
			observerActivity.showNotification(show);
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path, String comingFrom) {
		if (fetcher != null)
			fetcher.cancel();
		showNotification(true);
		this.comingFrom = comingFrom;
		fetcher = FileFetcher.fetchFiles(path, this, true, false);
	}
	
	public void setPrivateFileType(String fileType) {
		if (fetcher != null)
			fetcher.cancel();
		showNotification(true);
		fetcher = FileFetcher.fetchFiles(fileType, this, true, false);
	}
	
	public void cancel() {
		if (fetcher != null) {
			fetcher.cancel();
			fetcher = null;
			comingFrom = null;
			showNotification(false);
		}
	}
	
	@Override
	public void onFilesFetched(FileFetcher fetcher, Throwable e) {
		if (fetcher != this.fetcher)
			return;
		try {
			if (e != null)
				UI.toast(Player.getService(), e);
			items = fetcher.files;
			count = fetcher.count;
			path = fetcher.path;
			int p = 0;
			if (comingFrom != null && comingFrom.length() > 0) {
				if (path == null || path.length() == 0) {
					for (int i = count - 1; i >= 0; i--) {
						if (items[i].path.equals(comingFrom)) {
							p = i;
							break;
						}
					}
				} else {
					if (!path.startsWith(File.separator)) {
						for (int i = count - 1; i >= 0; i--) {
							if (items[i].path.startsWith(comingFrom)) {
								p = i;
								break;
							}
						}
					} else {
						for (int i = count - 1; i >= 0; i--) {
							if (items[i].name.equals(comingFrom)) {
								p = i;
								break;
							}
						}
					}
				}
			}
			setSelection(p, false);
		} finally {
			this.fetcher = null;
			comingFrom = null;
			showNotification(false);
		}
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		FileView view = ((convertView == null) ? observerActivity.createFileView() : (FileView)convertView);
		view.setItemState(items[position], position, getItemState(position));
		return view;
	}
}
