/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
#include <windows.h>
#include "stdafx.h"
#include "FluencyAPI.h"	// do not forget to also add FluencyAPI.cpp to your program!
#include <stdio.h>
#include "hmi_tts_fluency8_Fluency8TTSGenerator.h"

//#define LOG_INFO(x) printf(x)
#define LOG_INFO(x) 

bool fluencyDone;

HINSTANCE hFluencyDLL;

int Voices;
jobjectArray voices;
wchar_t VoiceName[200];
void *Voice;
void *Channel;
char *Phone;
void *Wav;
unsigned TextIndex;
unsigned Samples;
unsigned WordLength;
unsigned Bookmark;

jstring wsz2jstr(JNIEnv *env, wchar_t *str)
{
	int len = wcslen(str);
	jchar* raw = new jchar[len];
	memcpy(raw, str, len * sizeof(wchar_t));
	jstring result = env->NewString(raw, len);
	delete[] raw;
	return result;
}

int throwRuntimeException(JNIEnv *env, const char *exceptionDescription)
{
	jclass runtimeExceptionClass = env->FindClass("java/lang/RuntimeException");
	if(runtimeExceptionClass != NULL)
	{
		env->ThrowNew(runtimeExceptionClass, exceptionDescription);
		return 0;
	}
	else
	{
		fprintf(stderr,"Unable to construct RuntimeException");
		fprintf(stderr,exceptionDescription);
		return -1;
	}
}

void WINAPI FLUENCYSYNC(unsigned Event, unsigned Param1, unsigned Param2, unsigned User)
{
	switch (Event) {

	case SYNC_START:
		//printf("start\n");
		//pass on to the other internal callbacks? or immediately go to JNI?
		break;

	case SYNC_PROGRESS:
		break;

	case SYNC_FINISH:
		//printf("\nfinish\n");
		fluencyDone = TRUE;
		break;

	case SYNC_PHONEME:
		//get current phoneme
		char p[3];
		p[0] = (char)Param1;
		p[1] = (char)(Param1 >> 8);
		p[2] = (char)0;
		printf("%s ", p);
		Param1 = Param1 >> 16;
		//get next phoneme
		char pn[3];
		pn[0] = (char)Param1;
		pn[1] = (char)(Param1 >> 8);
		pn[2] = (char)0;
		//printf("(%s) ", pn);
		break;

	case SYNC_BOOKMARK:
		printf("<%d> ", Param1);
		break;

	}
}

JNIEXPORT jint JNICALL Java_hmi_tts_fluency8_Fluency8TTSGenerator_FluencyInit(JNIEnv *env, jobject obj)
{
	HRESULT hr;
	LOG_INFO("Fluency init\n");
	wchar_t Path[250];
	wchar_t Wrd[200];
	wchar_t NextWrd[200];
	wchar_t Transcription[200];
	int Voices;
	int i;
	MSG Msg;

	// load DLL
	hFluencyDLL = fluencyLoadDLL(L"");

	if (hFluencyDLL) {

		// test GetVersion (can be called before Initialize)
		//wprintf(L"Welcome to Fluency TTS %d.%d\n", fluencyGetMajorVersion(), fluencyGetMinorVersion());

		if (fluencyGetMajorVersion() < 8) wprintf(L"\nWARNING: This program requires Fluency TTS 8.0 or higher!\n");

		// initialize
		if (fluencyInitialize()) {

			// test AboutWindow
			//fluencyAboutWindow((HWND)0);

			// test fluencyGetUserDataPath
			//fluencyGetUserDataPathW(Path, 250 * sizeof(wchar_t));
			//wprintf(L"\nSettings directory:\n%s\n", Path);

			// test fluencyAddWord
			//fluencyAddWordW(L"muflon", L"m*u-flOn");

			// test fluencyLookupWord
			//fluencyLookupWordW(L"muflon", Transcription, 200 * sizeof(wchar_t));
			//wprintf(L"\n\nTest fluencyLookupWord()\nmuflon=%s\n", Transcription);

			// test fluencyUserLexiconNextWord
			//wprintf(L"\nTest fluencyUserLexiconNextWord()\n");
			//Wrd[0] = L'\0';
			//while (fluencyUserLexiconNextWordW(Wrd, NextWrd, 200 * sizeof(wchar_t))) {
				//wprintf(L"%s ", NextWrd);
				//wcscpy_s(Wrd, NextWrd);
			//}
			//wprintf(L"\n");

			// test enumerate voices
			//Voices = fluencyGetVoiceCount();
			//wprintf(L"\n%d voices:\n", Voices);
			//for (i = 1; i <= Voices; i++) {
				//fluencyGetVoiceNameW(i, VoiceName, 200 * sizeof(wchar_t));
				//wprintf(L"%s ", VoiceName);
			//}

			Voices = fluencyGetVoiceCount();
			voices = env->NewObjectArray(Voices, env->FindClass("java/lang/String"), env->NewStringUTF(""));
			//wprintf(L"\n%d voices:\n", Voices);
			for (int i = 1; i <= Voices; i++) {
				fluencyGetVoiceNameW(i, VoiceName, 200 * sizeof(wchar_t));
				//wprintf(L"%s \n", VoiceName);
				jstring str = wsz2jstr(env, VoiceName);
				env->SetObjectArrayElement(voices, i - 1, str);
				//to check that conversion back leads to right string again:
				/*
				const char *nativeString = env->GetStringUTFChars(str, 0);
				// use your string
				printf("... %s\n", nativeString);
				env->ReleaseStringUTFChars(str, nativeString);
				*/
			}

			// get preferred voice
			fluencyGetPreferredVoiceNameW(VoiceName, 200 * sizeof(wchar_t));
			wprintf(L"Fluency 8.0 initializing. \nPreferred voice: %s\n", VoiceName);

			// create voice and channel
			Voice = fluencyCreateVoiceW(VoiceName);
			Channel = fluencyCreateChannel(Voice, 0, TRUE);
			

		}
		else 
		{
			return -1;
		}
	}
	else
	{
		return -1;
	}
	return 0;
}

JNIEXPORT jint JNICALL Java_hmi_tts_fluency8_Fluency8TTSGenerator_FluencySetSpeaker(JNIEnv *env, jobject obj, jstring _speaker)
{
	void *OldVoice = Voice;
	wchar_t *NewVoiceName = (wchar_t*)((*env).GetStringChars(_speaker, 0));

	Voice = fluencyCreateVoiceW(NewVoiceName);
	if (Voice != NULL)
	{
		fluencySetVoice(Channel, Voice);
		fluencyDeleteVoice(OldVoice);
		return 0;
	}
	else
	{
		Voice = OldVoice;
		return -1;
	}
	
}

JNIEXPORT jobjectArray JNICALL Java_hmi_tts_fluency8_Fluency8TTSGenerator_FluencyGetVoices(JNIEnv *env, jobject obj)
{
	return voices;
}

					   
JNIEXPORT jint JNICALL Java_hmi_tts_fluency8_Fluency8TTSGenerator_FluencySpeakToFile (JNIEnv *env, jobject obj, jstring _text, jstring _filename)
{
	fluencyDone = FALSE;
	wchar_t *text = (wchar_t*)((*env).GetStringChars(_text, 0));
	wchar_t *filename = (wchar_t*)((*env).GetStringChars(_filename, 0));
	fluencySetInputTextW(Channel, text);
	fluencySpeakToFileW(Channel,filename, &FLUENCYSYNC, 0);
	// sleep while Fluency TTS is writing to file
	while (!fluencyDone) Sleep(10);

	return 0;
}



JNIEXPORT jint JNICALL Java_hmi_tts_fluency8_Fluency8TTSGenerator_FluencySpeak (JNIEnv *env, jobject obj, jstring _text)
{
	fluencyDone = FALSE;
	wchar_t *text = (wchar_t*)((*env).GetStringChars(_text, 0));
	fluencySetInputTextW(Channel, text);
	fluencySpeak(Channel, &FLUENCYSYNC, 0);
	MSG msg;

	// Fake Main message loop: fluencySpeak only works as long as emssages are being dispatched! strangely enough, this is not the case for the speaktofile...
	while (!fluencyDone && GetMessage(&msg, nullptr, 0, 0))
	{
		TranslateMessage(&msg);
		DispatchMessage(&msg);
	}
	
	return 0;
}

JNIEXPORT jint JNICALL Java_hmi_tts_fluency8_Fluency8TTSGenerator_FluencyCleanup(JNIEnv *env, jobject obj)
{
	// clean up
	fluencyDeleteChannel(Channel);
	fluencyDeleteVoice(Voice);
	fluencyClose();
	// free dll
	FreeLibrary(hFluencyDLL);

	return 0;
}