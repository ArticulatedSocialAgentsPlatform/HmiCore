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
#include <sapi.h>
#include <sphelper.h>
#include <stdio.h>
#include "hmi_tts_sapi5_SAPI5TTSGenerator.h"
ISpVoice  *cpVoice = NULL;

//#define LOG_INFO(x) printf(x)
#define LOG_INFO(x) 

const int LOQUENDO_STRESS_LENGTH = 89;
const int LOQUENDO_STRESS[]=
{
0x4E401000,
0x4E401700,
0x4E401780,
0x36A00000,
0x36A0E000,
0x36A0F000,
0x36A2E000,
0x36A2F000,
0xB4A00000,
0x50A00000,
0x50A03000,
0x62A00000,
0x62A03000,
0x62401180,
0x62A0E000,
0x62A0F000,
0x62401000,
0x62401700,
0x62401780,
0x016F8000,
0x01760000,
0x096BC000,
0x096F8000,
0x5F760000,
0x7D6BC000,
0x556F8000,
0x1D760000,
0xB16BC000,
0x7CA00000,
0x7CA0E000,
0x7CA0F000,
0x3CA00000,
0x3CA0E000,
0x3CA0F000,
0x3C401000,
0x3C401700,
0x3C401780,
0x64401000,
0x54A00000,
0x54A0E000,
0x54A0F000,
0x54401000,
0x42A00000,
0x42A03000,
0x42A0E000,
0x42A0F000,
0x42A2E000,
0x42A2F000,
0x30A00000,
0xBAA0E000,
0xBAA0F000,
0xB0A00000,
0xB0A0E000,
0xB0A0F000,
0xAE401700,
0xAE401780,
0xAE401700,
0xAE401780,
0x00A00000,
0x00A03000,
0x00C60000,
0x00A0E000,
0x00A0F000,
0x00401000,
0x08A00000,
0x08A03000,
0x08401000,
0x08401700,
0x08401780,
0x10A00000,
0x10A03000,
0x10401000,
0x10401700,
0x10401780,
0x1CA00000,
0x1CA03000,
0x1C401180,
0x1C401000,
0x1C401700,
0x1C401780,
0x28A00000,
0x28A03000,
0x28401000,
0x28401700,
0x28401780,
0x30A00000,
0x30401000,
0x30401700,
0x30401780
};

const int SAPI_VOWEL_LENGTH = 17;
const int SAPI_VOWEL[]=
{
	10,
	11,
	12,
	13,
	14,
	15,
	16,
	21,
	22,
	23,
	27,
	28,
	29,
	35,
	36,
	43,
	44,
};
const int LOQUENDO_VOWEL_LENGTH = 141;
const int LOQUENDO_VOWEL[]=
{
	0x4E401000,
	0x4E401700,
	0x4E401780,
	0x36A00000,
	0x36A0E000,
	0x36A0F000,
	0x36A2E000,
	0x36A2F000,
	0xB4A00000,
	0x50A00000,
	0x50A03000,
	0x62A00000,
	0x62A03000,
	0x62401180,
	0x62A0E000,
	0x62A0F000,
	0x62401000,
	0x62401700,
	0x62401780,
	0x016F8000,
	0x01760000,
	0x096BC000,
	0x096F8000,
	0x5F760000,
	0x7D6BC000,
	0x556F8000,
	0x1D760000,
	0xB16BC000,
	0x7CA00000,
	0x7CA0E000,
	0x7CA0F000,
	0x3CA00000,
	0x3CA0E000,
	0x3CA0F000,
	0x3C401000,
	0x3C401700,
	0x3C401780,
	0x64401000,
	0x54A00000,
	0x54A0E000,
	0x54A0F000,
	0x54401000,
	0x42A00000,
	0x42A03000,
	0x42A0E000,
	0x42A0F000,
	0x42A2E000,
	0x42A2F000,
	0x30A00000,
	0xBAA0E000,
	0xBAA0F000,
	0xB0A00000,
	0xB0A0E000,
	0xB0A0F000,
	0xAE401700,
	0xAE401780,
	0xAE401700,
	0xAE401780,
	0x00A00000,
	0x00A03000,
	0x00C60000,
	0x00A0E000,
	0x00A0F000,
	0x00401000,
	0x08A00000,
	0x08A03000,
	0x08401000,
	0x08401700,
	0x08401780,
	0x10A00000,
	0x10A03000,
	0x10401000,
	0x10401700,
	0x10401780,
	0x1CA00000,
	0x1CA03000,
	0x1C401180,
	0x1C401000,
	0x1C401700,
	0x1C401780,
	0x28A00000,
	0x28A03000,
	0x28401000,
	0x28401700,
	0x28401780,
	0x30A00000,
	0x30401000,
	0x30401700,
	0x30401780,
	0x4CC00000,
	0x4EA20000,
	0x36C00000,
	0xB4C00000,
	0x50C00000,
	0x50C60000,
	0x62C00000,
	0x62C60000,
	0x62A23000,
	0x62A20000,
	0x01BE0000,
	0x01D80000,
	0x09AF0000,
	0x09BE0000,
	0x5FD80000,
	0x7DAF0000,
	0x55BE0000,
	0x1DD80000,
	0xB1AF0000,
	0x7CC00000,
	0x3CC00000,
	0x3CA20000,
	0x64A20000,
	0x54C00000,
	0x54A20000,
	0x42C00000,
	0x42C60000,
	0xBAC00000,
	0xB0C00000,
	0xAEC00000,
	0x00C00000,
	0x00C60000,
	0x00A23000,
	0x00A20000,
	0x00B70000,
	0x08C00000,
	0x08C60000,
	0x08A20000,
	0x08B70000,
	0x10C00000,
	0x10C60000,
	0x10A20000,
	0x10B70000,
	0x1CC00000,
	0x1CC60000,
	0x1CA23000,
	0x1CA20000,
	0x1CB70000,
	0x28C00000,
	0x28C60000,
	0x28A20000,
	0x28B70000,
};

/*
JNIEnv *environment;
jobject object;
jmethodID wordCallbackMid;		
jmethodID phonemeCallbackMid;	
jmethodID bookmarkCallbackMid;	
boolean fDone = false;
void __stdcall callback(WPARAM wParam, LPARAM lParam)
{
	HRESULT hr;
	int phoneme,nextPhoneme,duration,stress,vowel,i;
	SPEVENT event;
	ULONG ul;
	printf("Callback\n");
	while (cpVoice->GetEvents(1, &event, &ul) == S_OK)
	{
		if (event.eEventId == SPEI_PHONEME)
		{
			
			phoneme = event.lParam&0xffff;
			nextPhoneme = event.wParam&0xffff;
			stress = event.lParam>>16;
			duration = event.wParam>>16;
			vowel = 0;
			//assume Loquendo
			if(phoneme>49)
			{	
				phoneme = (phoneme<<16)|nextPhoneme;
				nextPhoneme = 0;
				stress = 0;
				for (i = 0;i<LOQUENDO_STRESS_LENGTH;i++)
				{
					if(phoneme==LOQUENDO_STRESS[i])
					{
						stress = 1;
						break;
					}
				}
				vowel=0;
				for(i=0;i<LOQUENDO_VOWEL_LENGTH;i++)
				{
					if(phoneme==LOQUENDO_VOWEL[i])
					{
						vowel = 1;
						break;
					}
				}
			}
			else
			{
				vowel=0;
				for(i=0;i<SAPI_VOWEL_LENGTH;i++)
				{
					if(phoneme==SAPI_VOWEL[i])
					{
						vowel = 1;
						break;
					}
				}
			}
			//phonemeCallback(phoneme,duration,nextphoneme,stress,vowel)				
			(*environment).CallVoidMethod(object, phonemeCallbackMid, phoneme,duration,nextPhoneme,stress,vowel);
		}
		else if (event.eEventId == SPEI_WORD_BOUNDARY)
		{
			(*environment).CallVoidMethod(object, wordCallbackMid, event.lParam,event.wParam);						
		}		
		else if (event.eEventId == SPEI_TTS_BOOKMARK)
		{
			const WCHAR *bookmark = (const WCHAR *) (event.lParam);
			jstring str = (*environment).NewString((const jchar*)bookmark,wcslen(bookmark));			
			(*environment).CallVoidMethod(object, bookmarkCallbackMid,str);			 			
		}
		else if (event.eEventId == SPEI_END_INPUT_STREAM)
		{
			fDone = true;
		}
	}
}
*/

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

JNIEXPORT jint JNICALL Java_hmi_tts_sapi5_SAPI5TTSGenerator_SAPIInit(JNIEnv *env, jobject obj)
{
	HRESULT hr;
	LOG_INFO("SAPI init\n");
	

	//if (::CoInitializeEx(NULL,COINIT_MULTITHREADED) == RPC_E_CHANGED_MODE)
	hr = ::CoInitializeEx(NULL,COINIT_APARTMENTTHREADED);
	if ( hr != S_OK && hr != S_FALSE)
	{
		return throwRuntimeException(env,"Unable to initialize OLE\n");		
	}

	hr = CoCreateInstance(CLSID_SpVoice, NULL, CLSCTX_ALL,IID_ISpVoice, (void **)&cpVoice);
	if (FAILED(hr))
	{
		return throwRuntimeException(env,"Unable to initialize CLSID_SpVoice\n");		
	}

	hr = cpVoice->SetNotifyWin32Event(); // State that notifications will be done by calling WaitForNotifyEvent
	if (FAILED(hr))
	{
		return throwRuntimeException(env,"Unable to set notify\n");		
	}	
	return 0;
}

JNIEXPORT jint JNICALL Java_hmi_tts_sapi5_SAPI5TTSGenerator_SAPISetSpeaker(JNIEnv *env, jobject obj, jstring _speaker)
{
	
	HRESULT hr;
	IEnumSpObjectTokens *cpEnumVoci=NULL;
	ISpObjectToken *cpVoiceToken=NULL;    
	char speakerName[1024];
	const char *speaker = (*env).GetStringUTFChars(_speaker, 0);
	sprintf(speakerName,"Name=%s",speaker);
	WCHAR wszSpeakerName[1024]; 
	
	LOG_INFO("SAPISetSpeaker\n");
	
	MultiByteToWideChar( CP_ACP, 0, speakerName, strlen(speakerName)+1, wszSpeakerName, sizeof(wszSpeakerName)/sizeof(wszSpeakerName[0]) );
	hr = SpEnumTokens(SPCAT_VOICES, wszSpeakerName, NULL, &cpEnumVoci);
	if (FAILED(hr))
	{
		fprintf(stderr,"Unable to enumerate voice's token\n");
		(*env).ReleaseStringUTFChars(_speaker, speaker);
		return -1;
	}

	hr = cpEnumVoci->Next( 1, &cpVoiceToken, NULL );
	if (FAILED(hr))
	{
		fprintf(stderr,"Unable to find desidered token\n");
		(*env).ReleaseStringUTFChars(_speaker, speaker);
		return -1;
	}

	hr = cpVoice->SetVoice( cpVoiceToken);
	if (FAILED(hr))
	{
		fprintf(stderr,"Unable to set desidered voice\n");
		(*env).ReleaseStringUTFChars(_speaker, speaker);
		return -1;
	}
	(*env).ReleaseStringUTFChars(_speaker, speaker);
	return 0;
}

JNIEXPORT jobjectArray JNICALL Java_hmi_tts_sapi5_SAPI5TTSGenerator_SAPIGetVoices(JNIEnv *env, jobject obj)
{
	CComPtr<IEnumSpObjectTokens>   cpEnum;
	HRESULT                        hr = S_OK;
	CComPtr<ISpObjectToken>        cpVoiceToken;
	ULONG                          ulCount = 0;
	WCHAR						   *name = NULL;
	// Enumerate the available voices.
	hr = SpEnumTokens(SPCAT_VOICES, NULL, NULL, &cpEnum);

	if (SUCCEEDED (hr))
	{
		// Get the number of voices.
		hr = cpEnum->GetCount(&ulCount);
	}
	jobjectArray voices = env->NewObjectArray(ulCount, env->FindClass("java/lang/String"), env->NewStringUTF(""));
	int i=0;
	if(SUCCEEDED (hr))
	{
		while (SUCCEEDED(hr) && ulCount--)
		{
			hr = cpEnum->Next(1, &cpVoiceToken, NULL);
			if(SUCCEEDED (hr))
			{
				CComPtr<ISpDataKey> cpAttribKey;
				hr = cpVoiceToken->OpenKey(L"Attributes", &cpAttribKey);				
				if(SUCCEEDED (hr))
				{
					hr = cpAttribKey->GetStringValue(L"Name",&name);
					if(SUCCEEDED (hr))
					{
						jstring str = (*env).NewString((const jchar*)name,wcslen(name));
						env->SetObjectArrayElement(voices,i,str);
						i++;
						::CoTaskMemFree(name);
					}
				}
			}			
		}		
	}
	return voices;
}

int speak(JNIEnv *env, jobject obj,const WCHAR *text)
{
	HRESULT hr;
	ISpStreamFormat *cpCurrentStreamFormat;
	GUID           pguidCurrentFormatId;
	WAVEFORMATEX  *ppCurrentCoMemWaveFormatEx;

	int phoneme,nextPhoneme,duration,stress;
	hr = cpVoice->SetInterest(SPFEI(SPEI_TTS_BOOKMARK)|SPFEI(SPEI_PHONEME) |SPFEI(SPEI_WORD_BOUNDARY)|
	SPFEI(SPEI_END_INPUT_STREAM)|SPFEI(SPEI_VISEME)|SPFEI(SPEI_SENTENCE_BOUNDARY),SPFEI(SPEI_TTS_BOOKMARK)|SPFEI(SPEI_PHONEME) |SPFEI(SPEI_WORD_BOUNDARY)|
	SPFEI(SPEI_END_INPUT_STREAM)|SPFEI(SPEI_VISEME)|SPFEI(SPEI_SENTENCE_BOUNDARY));
	/*
	hr = cpVoice->Speak(text, SPF_ASYNC|SPF_IS_XML, NULL); // Speak asynchronously
	*/

	ULONG ul;
	SPEVENT event;
	jclass cls = (*env).GetObjectClass(obj);
    jmethodID wordCallbackMid = (*env).GetMethodID(cls, "wordBoundryCallback", "(II)V");
	jmethodID sentenceCallbackMid = (*env).GetMethodID(cls, "sentenceBoundryCallback", "(II)V");
	jmethodID phonemeCallbackMid = (*env).GetMethodID(cls, "phonemeCallback", "(IIII)V");
	jmethodID visimeCallbackMid = (*env).GetMethodID(cls, "visimeCallback", "(IIII)V");
	jmethodID bookmarkCallbackMid = (*env).GetMethodID(cls, "bookmarkCallback", "(Ljava/lang/String;)V");
	jmethodID stopCallbackMid = (*env).GetMethodID(cls, "stopCallback", "()Z");
	jmethodID getVolumeMid = (*env).GetMethodID(cls, "getVolume", "()I");
	jmethodID getRateMid = (*env).GetMethodID(cls, "getRate", "()I");

    if (wordCallbackMid == 0 || phonemeCallbackMid==0 || bookmarkCallbackMid==0 || visimeCallbackMid==0|| sentenceCallbackMid==0 ||stopCallbackMid==0) 
	{
        fprintf(stderr,"Can't find java callback methods\n");
		return -1;
    } 
	//cpVoice->SetNotifyCallbackFunction(callback,wp,lp);
	//cpVoice->SetNotifyWin32Event();
	//cpVoice->GetNotifyEventHandle();

	hr = cpVoice->Speak(text, SPF_ASYNC|SPF_IS_XML, NULL);
	if(FAILED(hr))
	{
		fprintf(stderr,"Failure in cpVoice->Speak\n");
		return -1;
	}
	//hr = cpVoice->Speak(text, SPF_IS_XML, NULL);
	bool fDone = false;
	//while (!fDone);

	ISpStream *cpNullStream;		// Pass-through object between the voice and file stream object
	hr = CoCreateInstance(CLSID_SpStream , NULL, CLSCTX_ALL, IID_ISpStream,(void**)&cpNullStream);		
	if(FAILED(hr))
	{
		fprintf(stderr,"Failure When creating the null stream\n");
		return -1;
	}
	hr = cpVoice->GetOutputStream(&cpCurrentStreamFormat);
	if(FAILED(hr))
	{
		fprintf(stderr,"Failure GetOutputStream(cpCurrentStream)\n");
		return -1;
	}	
	cpCurrentStreamFormat->GetFormat(&pguidCurrentFormatId,&ppCurrentCoMemWaveFormatEx);
	if(FAILED(hr))
	{
		fprintf(stderr,"Failure GetFormat(&pguidCurrentFormatId,&ppCurrentCoMemWaveFormatEx)\n");
		return -1;
	}
	hr = cpNullStream->SetBaseStream(cpCurrentStreamFormat,pguidCurrentFormatId,ppCurrentCoMemWaveFormatEx);
	if(FAILED(hr))
	{
		fprintf(stderr,"Failure SetBaseStream(cpCurrentBaseStream)\n");
		return -1;
	}

	bool stopped = false;
    while (!fDone)
	{
	if ((*env).CallBooleanMethod(obj, stopCallbackMid))
	{
		hr = cpVoice->SetOutput(cpNullStream, FALSE);	
		if(FAILED(hr))
		{
			fprintf(stderr,"Failure setting output to the null stream\n");
			return -1;
		}
		//cpVoice->Pause();	
		
		//ULONG skipped;
		//cpVoice->Skip(L"SENTENCE",1000, &skipped);
		
		stopped = true;
	}

	USHORT desiredVolume = (*env).CallIntMethod(obj,getVolumeMid);
	USHORT currentVolume;
	cpVoice->GetVolume(&currentVolume);
	if(desiredVolume!=currentVolume)
	{
		hr = cpVoice->SetVolume(desiredVolume);
		if(FAILED(hr))
		{
			fprintf(stderr,"Failure setting volume\n");
			return -1;
		}
	}

	long desiredRate = (*env).CallIntMethod(obj,getRateMid);
	long currentRate;
	cpVoice->GetRate(&currentRate);
	if(desiredRate!=currentRate)
	{
		hr = cpVoice->SetRate(desiredRate);
		if(FAILED(hr))
		{
			fprintf(stderr,"Failure setting rate %d\n", desiredRate);
			return -1;
		}
	}

	hr = cpVoice->WaitForNotifyEvent(INFINITE); // Wait for some events
	if(FAILED(hr))
	{
		fprintf(stderr,"Failure WaitForNotifyEvent\n");
		return -1;
	}

	while (cpVoice->GetEvents(1, &event, &ul) == S_OK)
	{
		if ((*env).CallBooleanMethod(obj, stopCallbackMid))
		{
			//Skip senteces, apperently does not react as fast as setting output to the null stream
			//ULONG skipped
			//cpVoice->Skip(L"SENTENCE",1000, &skipped);
			
			//pause, speak to next word/phoneme/whatever set with SetAlertBoundary
			//cpVoice->Pause();				
			
			//change to empty pass-through stream
			hr = cpVoice->SetOutput(cpNullStream, FALSE);			
			if(FAILED(hr))
			{
				fprintf(stderr,"Failure setting output to null stream\n");
				return -1;
			}
			stopped = true;
		}
			
		if (event.eEventId == SPEI_PHONEME && !stopped)
		{
			phoneme = event.lParam&0xffff;
			nextPhoneme = event.wParam&0xffff;
			stress = event.lParam>>16;
			duration = event.wParam>>16;
			/*
			//assume Loquendo
			if(phoneme>49)
			{	
				phoneme = (phoneme<<16)|nextPhoneme;
				nextPhoneme = 0;
				stress = 0;
				for (i = 0;i<LOQUENDO_STRESS_LENGTH;i++)
				{
					if(phoneme==LOQUENDO_STRESS[i])
					{
						stress = 1;
						break;
					}
				}
				vowel=0;
				for(i=0;i<LOQUENDO_VOWEL_LENGTH;i++)
				{
					if(phoneme==LOQUENDO_VOWEL[i])
					{
						vowel = 1;
						break;
					}
				}
			}
			else
			
			{
				for(i=0;i<SAPI_VOWEL_LENGTH;i++)
				{
					if(phoneme==SAPI_VOWEL[i])
					{
						vowel = 1;
						break;
					}
				}
			}
			*/
			//phonemeCallback(phoneme,duration,nextphoneme,stress,vowel)					
			(*env).CallVoidMethod(obj, phonemeCallbackMid, phoneme,duration,nextPhoneme,stress);
		}
		else if (event.eEventId == SPEI_WORD_BOUNDARY && !stopped)
		{
			(*env).CallVoidMethod(obj, wordCallbackMid, event.lParam,event.wParam);						
		}		
		else if (event.eEventId == SPEI_TTS_BOOKMARK && !stopped)
		{
			const WCHAR *bookmark = (const WCHAR *) (event.lParam);
			jstring str = (*env).NewString((const jchar*)bookmark,wcslen(bookmark));			
			(*env).CallVoidMethod(obj, bookmarkCallbackMid,str);			 			
		}
		else if (event.eEventId == SPEI_VISEME && !stopped)
		{
			phoneme = event.lParam&0xffff;
			nextPhoneme = event.wParam&0xffff;
			stress = event.lParam>>16;
			duration = event.wParam>>16;
			(*env).CallVoidMethod(obj, visimeCallbackMid, phoneme,duration,nextPhoneme,stress);
		}
		else if (event.eEventId == SPEI_SENTENCE_BOUNDARY && !stopped)
		{
			(*env).CallVoidMethod(obj, sentenceCallbackMid, event.lParam,event.wParam);
		}
		else if (event.eEventId == SPEI_END_INPUT_STREAM)
		{
			fDone = true; // Stream ended
		}
	}
	}	
	hr = cpNullStream->Close();
	if(FAILED(hr))
	{
		fprintf(stderr,"Failure closing cpNullStream %x hex\n",HRESULT_CODE(hr));
		return -1;
	}
	return 0;
}

JNIEXPORT jint JNICALL Java_hmi_tts_sapi5_SAPI5TTSGenerator_SAPIDummySpeak (JNIEnv *env, jobject obj, jstring _text)
{
	HRESULT hr;
	ISpStream *cpStream;		// Pass-through object between the voice and file stream object
	IStream *cpBaseStream=NULL;
	
	LOG_INFO("Start dummyspeak\n");
	
	CSpStreamFormat format(SPSF_22kHz16BitMono, &hr); //set the format for the wav
	//CSpStreamFormat format(SPSF_22kHz8BitMono, &hr); //set the format for the wav

	const WCHAR *text = (const WCHAR*)((*env).GetStringChars(_text, 0));
	
	hr = CoCreateInstance(CLSID_SpStream , NULL, CLSCTX_ALL, IID_ISpStream,(void**)&cpStream);	
	if(hr!=S_OK)
	{
		fprintf(stderr,"Error creating cpStream in dummy speak: %x (hex)\n",HRESULT_CODE(hr));
		return -1;
	}
	LOG_INFO("Created cpStream\n");

	//create 'dummy' memory stream
	GUID guidFormat; 
	WAVEFORMATEX* pWavFormatEx;
	
	//hr = CreateStreamOnHGlobal(NULL, FALSE, &cpBaseStream);
	hr = CreateStreamOnHGlobal(NULL, TRUE, &cpBaseStream);	
	if(hr!=S_OK)
	{
		fprintf(stderr,"Error creating cpBaseStream in dummy speak: %x hex\n",HRESULT_CODE(hr));
		return -1;
	}
	LOG_INFO("Created cpBaseStream\n");

	hr = SpConvertStreamFormatEnum(SPSF_22kHz16BitMono, &guidFormat,&pWavFormatEx);
	if(hr!=S_OK)
	{
		fprintf(stderr,"Error creating output format in dummy speak: %x hex\n",HRESULT_CODE(hr));
		return -1;
	}
	LOG_INFO("Created output format\n");

	hr = cpStream->SetBaseStream(cpBaseStream, guidFormat,pWavFormatEx);
	if(hr!=S_OK)
	{
		fprintf(stderr,"Error linking cpStream to cpBaseStream: %x hex\n",HRESULT_CODE(hr));
		return -1;
	}
	LOG_INFO("Linked cpStream to cpBaseStream\n");

	// Connect the voice to the pass-through stream
	hr = cpVoice->SetOutput(cpStream, FALSE);
	if(hr!=S_OK)
	{
		fprintf(stderr,"Error setting cpStream: %x hex\n",HRESULT_CODE(hr));
		return -1;
	}
	LOG_INFO("Set up cpStream, starting speak\n");

	int result = speak(env,obj,text);
	LOG_INFO("Speak done, cleaning up\n");

	hr = cpStream->Close();
	if(hr!=S_OK)
	{
		fprintf(stderr,"Failure closing cpStream: %x hex\n",HRESULT_CODE(hr));
		return -1;
	}

	if (cpBaseStream)
	{
		hr = cpBaseStream->Release();
		if(hr!=S_OK)
		{
			fprintf(stderr,"Failure cpBaseStream->Release(): %x hex\n",HRESULT_CODE(hr));
			return -1;
		}
	}
	(*env).ReleaseStringChars(_text, (jchar*)text);
	return result;
}

					   
JNIEXPORT jint JNICALL Java_hmi_tts_sapi5_SAPI5TTSGenerator_SAPISpeakToFile (JNIEnv *env, jobject obj, jstring _text, jstring _filename)
{
	HRESULT hr;
	ISpStream *cpStream;		// Pass-through object between the voice and file stream object
	ISpStream *cpFileStream;	// Actually generates wave file
	


	CSpStreamFormat format(SPSF_22kHz16BitMono, &hr); //set the format for the wav
	//CSpStreamFormat format(SPSF_22kHz8BitMono, &hr); //set the format for the wav

	const WCHAR *text = (const WCHAR*)((*env).GetStringChars(_text, 0));
	const WCHAR *filename = (const WCHAR*)((*env).GetStringChars(_filename, 0));
	hr = CoCreateInstance(CLSID_SpStream , NULL, CLSCTX_ALL, IID_ISpStream,(void**)&cpStream);			
	if(hr!=S_OK)
	{
		fprintf(stderr,"Failure CoCreateInstance: %x hex\n",HRESULT_CODE(hr));
		(*env).ReleaseStringChars(_filename, (jchar*)filename);
		(*env).ReleaseStringChars(_text, (jchar*)text);
		return -1;
	}

	// Create wav file stream
	hr = SPBindToFile(filename,  SPFM_CREATE_ALWAYS,  &cpFileStream, &format.FormatId(),format.WaveFormatExPtr() );
	if(FAILED(hr))
	{
		fprintf(stderr,"Unable to bind file\n");
		(*env).ReleaseStringChars(_filename, (jchar*)filename);
		(*env).ReleaseStringChars(_text, (jchar*)text);
		return -1;
	}

	// Create pass-through stream and set its base stream to be the file stream
	hr = cpStream->SetBaseStream(cpFileStream, format.FormatId(),format.WaveFormatExPtr());
	if(hr!=S_OK)
	{
		fprintf(stderr,"Failure SetBaseStream: %x hex\n",HRESULT_CODE(hr));
		(*env).ReleaseStringChars(_filename, (jchar*)filename);
		(*env).ReleaseStringChars(_text, (jchar*)text);
		return -1;
	}
	
	// Connect the voice to the pass-through stream
	hr = cpVoice->SetOutput(cpStream, FALSE);
	if(hr!=S_OK)
	{
		fprintf(stderr,"Failure SetOutput: %x hex\n",HRESULT_CODE(hr));
		(*env).ReleaseStringChars(_filename, (jchar*)filename);
		(*env).ReleaseStringChars(_text, (jchar*)text);
		return -1;
	}

	int result = speak(env,obj,text);
	

	hr = cpFileStream->Close();
	if(hr!=S_OK)
	{
		fprintf(stderr,"Failure cpFileStream->Close(): %x hex\n",HRESULT_CODE(hr));
		(*env).ReleaseStringChars(_filename, (jchar*)filename);
		(*env).ReleaseStringChars(_text, (jchar*)text);
		return -1;
	}
	cpStream->Close();
	if(hr!=S_OK)
	{
		fprintf(stderr,"Failure cpStream->Close(): %x hex\n",HRESULT_CODE(hr));
		(*env).ReleaseStringChars(_filename, (jchar*)filename);
		(*env).ReleaseStringChars(_text, (jchar*)text);
		return -1;
	}

	(*env).ReleaseStringChars(_filename, (jchar*)filename);
	(*env).ReleaseStringChars(_text, (jchar*)text);
	return result;
}



JNIEXPORT jint JNICALL Java_hmi_tts_sapi5_SAPI5TTSGenerator_SAPISpeak (JNIEnv *env, jobject obj, jstring _text)
{
	HRESULT hr;
	const WCHAR *text = (WCHAR*)((*env).GetStringChars(_text, 0));
	hr = cpVoice->SetOutput(NULL, FALSE);
	int result = -1;
	if(!FAILED(hr))
	{
		result = speak(env,obj,text);	
	}
	else
	{
		fprintf(stderr,"Unable to set voice output\n");
	}
	(*env).ReleaseStringChars(_text, (jchar*)text);
	return result ;
}

JNIEXPORT jint JNICALL Java_hmi_tts_sapi5_SAPI5TTSGenerator_SAPICleanup(JNIEnv *env, jobject obj)
{
	LOG_INFO("SAPICleanup\n");
	if(cpVoice)
	{
		cpVoice->Release();
		cpVoice = NULL;
	}
    ::CoUninitialize();
	return 0;
}