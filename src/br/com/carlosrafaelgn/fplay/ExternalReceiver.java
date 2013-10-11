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
package br.com.carlosrafaelgn.fplay;

import br.com.carlosrafaelgn.fplay.activity.MainHandler;
import br.com.carlosrafaelgn.fplay.playback.Player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

//
//Allowing applications to play nice(r) with each other: Handling remote control buttons
//http://android-developers.blogspot.com.br/2010/06/allowing-applications-to-play-nicer.html
//
public final class ExternalReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null || !Player.isInitialized())
			return;
		final String a = intent.getAction();
		if (a == null)
			return;
		if (a.equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
			if (Player.isFocused()) {
				abortBroadcast();
				Player.registerMediaButtonEventReceiver();
			}
			if (MainHandler.isOnMainThread()) {
				Player.becomingNoisy();
			} else {
				MainHandler.post(new Runnable() {
					@Override
					public void run() {
						Player.becomingNoisy();
					}
				});
			}
		} else if (a.equals(Intent.ACTION_MEDIA_BUTTON)) {
			final Object o = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if (o == null || !(o instanceof KeyEvent))
				return;
			final KeyEvent e = (KeyEvent)o;
			if (e.getAction() != KeyEvent.ACTION_DOWN) //ACTION_MULTIPLE...?
				return;
			final int k = e.getKeyCode();
			if (MainHandler.isOnMainThread()) {
				Player.handleMediaButton(k);
			} else {
				MainHandler.post(new Runnable() {
					@Override
					public void run() {
						Player.handleMediaButton(k);
					}
				});
			}
		}
	}
}