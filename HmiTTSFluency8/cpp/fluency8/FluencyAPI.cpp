// FluencyAPI.cpp
//
// C/C++ wrapper for Fluency API
// Copyright (C) 2008-2015 Fluency, Amsterdam

// see www.fluency.nl/api for documentation

// 2015 - adapted for Fluency TTS 8.0 (32-bit/64-bit, ansi/unicode)

#include "stdafx.h"
#include <stdio.h>
#include "FluencyAPI.h"

// type definitions for API functions

// version info
typedef unsigned (WINAPI *fluencyGetVersionPROC)(void);
typedef void (WINAPI *fluencyAboutWindowPROC)(HWND Win);

// initialize/close
typedef void (WINAPI *fluencyUnlockPROC)(char *Key, char *Extra);
typedef void (WINAPI *fluencyUnlockWPROC)(wchar_t *Key, wchar_t *Extra);
typedef bool (WINAPI *fluencyInitializePROC)(void);
typedef bool (WINAPI *fluencyInitializeFromPROC)(char *TTSPath, char *UserPath);
typedef bool (WINAPI *fluencyInitializeFromWPROC)(wchar_t *TTSPath, wchar_t *UserPath);
typedef void (WINAPI *fluencyClosePROC)(void);

// voice info
typedef unsigned (WINAPI *fluencyGetVoiceCountPROC)(void);
typedef unsigned (WINAPI *fluencyGetVoiceNamePROC)(unsigned Voice, char *Name, unsigned MaxBytes);
typedef unsigned (WINAPI *fluencyGetVoiceNameWPROC)(unsigned Voice, wchar_t *Name, unsigned MaxBytes);

// voice object
typedef void *(WINAPI *fluencyCreateVoicePROC)(char *Name);
typedef void *(WINAPI *fluencyCreateVoiceWPROC)(wchar_t *Name);
typedef void (WINAPI *fluencyDeleteVoicePROC)(void *Voice);

// channel object
typedef void *(WINAPI *fluencyCreateChannelPROC)(void *Voice, unsigned SamplingRate, bool Stereo);
typedef void (WINAPI *fluencyDeleteChannelPROC)(void *Channel);
typedef void (WINAPI *fluencySetVoicePROC)(void *Channel, void *Voice);
typedef void (WINAPI *fluencySetTempoPROC)(void *Channel, int Tempo);
typedef void (WINAPI *fluencySetVolumePROC)(void *Channel, unsigned Volume);
typedef void (WINAPI *fluencySetBalancePROC)(void *Channel, int Balance);
typedef void (WINAPI *fluencySetParameterPROC)(void *Channel, char *Name, char *Value);
typedef void (WINAPI *fluencySetParameterWPROC)(void *Channel, wchar_t *Name, wchar_t *Value);
typedef void (WINAPI *fluencySetInputTextPROC)(void *Channel, char *Text);
typedef void (WINAPI *fluencySetInputTextWPROC)(void *Channel, wchar_t *Text);
typedef bool (WINAPI *fluencyGetPhonePROC)(void *Channel, char **Phone, unsigned *Samples, void **Wav, unsigned *TextIndex, unsigned *WordLength);
typedef bool (WINAPI *fluencyGetBookmarkPROC)(void *Channel, unsigned *Bookmark);
typedef bool (WINAPI *fluencySpeakPROC)(void *Channel, void *Sync, unsigned User);
typedef void (WINAPI *fluencyStopSpeakingPROC)(void *Channel);
typedef void (WINAPI *fluencyPauseResumeSpeakingPROC)(void *Channel);
typedef void (WINAPI *fluencySpeakToFilePROC)(void *Channel, char *Filename, void *Sync, unsigned User);
typedef void (WINAPI *fluencySpeakToFileWPROC)(void *Channel, wchar_t *Filename, void *Sync, unsigned User);
typedef void (WINAPI *fluencyAbortSpeakToFilePROC)(void *Channel);
typedef void (WINAPI *fluencyScanTextPROC)(void *Channel, unsigned Sensitivity, void *Sync, unsigned User);
typedef void (WINAPI *fluencyAbortScanTextPROC)(void *Channel);

// lexicon
typedef unsigned (WINAPI *fluencyLookupWordPROC)(char *Word, char *Transcription, unsigned MaxBytes);
typedef unsigned (WINAPI *fluencyLookupWordWPROC)(wchar_t *Word, wchar_t *Transcription, unsigned MaxBytes);
typedef void (WINAPI *fluencyAddWordPROC)(char *Word, char *Transcription);
typedef void (WINAPI *fluencyAddWordWPROC)(wchar_t *Word, wchar_t *Transcription);
typedef unsigned (WINAPI *fluencyUserLexiconNextWordPROC)(char *Word, char *NextWord, unsigned MaxBytes);
typedef unsigned (WINAPI *fluencyUserLexiconNextWordWPROC)(wchar_t *Word, wchar_t *NextWord, unsigned MaxBytes);
typedef void (WINAPI *fluencyUserLexiconClearPROC)(void);
typedef void (WINAPI *fluencyUserLexiconImportExportPROC)(char *Filename);
typedef void (WINAPI *fluencyUserLexiconImportExportWPROC)(wchar_t *Filename);

// settings
typedef unsigned (WINAPI *fluencyGetPreferredVoiceNamePROC)(char *Name, unsigned MaxBytes);
typedef unsigned (WINAPI *fluencyGetPreferredVoiceNameWPROC)(wchar_t *Name, unsigned MaxBytes);
typedef void (WINAPI *fluencySetPreferredVoiceNamePROC)(char *Name);
typedef void (WINAPI *fluencySetPreferredVoiceNameWPROC)(wchar_t *Name);
typedef int (WINAPI *fluencyGetPreferredTempoPROC)(void);
typedef void (WINAPI *fluencySetPreferredTempoPROC)(int Tempo);
typedef unsigned (WINAPI *fluencyGetPreferredVolumePROC)(void);
typedef void (WINAPI *fluencySetPreferredVolumePROC)(unsigned Volume);
typedef int (WINAPI *fluencyGetPreferredBalancePROC)(void);
typedef void (WINAPI *fluencySetPreferredBalancePROC)(int Balance);
typedef unsigned (WINAPI *fluencyGetModePROC)(void);
typedef void (WINAPI *fluencySetModePROC)(unsigned Mode);
typedef bool (WINAPI *fluencyGetPunctuationModePROC)(void);
typedef void (WINAPI *fluencySetPunctuationModePROC)(bool Mode);
typedef void (WINAPI *fluencySaveSettingsPROC)(void);
typedef void (WINAPI *fluencyUpdateSettingsPROC)(void);
typedef unsigned (WINAPI *fluencyGetUserDataPathPROC)(char *Path, unsigned MaxBytes);
typedef unsigned (WINAPI *fluencyGetUserDataPathWPROC)(wchar_t *Path, unsigned MaxBytes);
typedef bool (WINAPI *fluencySetFrisianModePROC)(bool OnOff);
  
// API function pointers

// version info
fluencyGetVersionPROC fluencyGetMajorVersionPTR = NULL;
fluencyGetVersionPROC fluencyGetMinorVersionPTR = NULL;
fluencyAboutWindowPROC fluencyAboutWindowPTR = NULL;

// initialize/close
fluencyUnlockPROC fluencyUnlockPTR = NULL;
fluencyUnlockWPROC fluencyUnlockWPTR = NULL;
fluencyInitializePROC fluencyInitializePTR = NULL;
fluencyInitializeFromPROC fluencyInitializeFromPTR = NULL;
fluencyInitializeFromWPROC fluencyInitializeFromWPTR = NULL;
fluencyClosePROC fluencyClosePTR = NULL;

// voice info
fluencyGetVoiceCountPROC fluencyGetVoiceCountPTR = NULL;
fluencyGetVoiceNamePROC fluencyGetVoiceNamePTR = NULL;
fluencyGetVoiceNameWPROC fluencyGetVoiceNameWPTR = NULL;

// voice object
fluencyCreateVoicePROC fluencyCreateVoicePTR = NULL;
fluencyCreateVoiceWPROC fluencyCreateVoiceWPTR = NULL;
fluencyDeleteVoicePROC fluencyDeleteVoicePTR = NULL;

// channel object
fluencyCreateChannelPROC fluencyCreateChannelPTR = NULL;
fluencyDeleteChannelPROC fluencyDeleteChannelPTR = NULL;
fluencySetVoicePROC fluencySetVoicePTR = NULL;
fluencySetTempoPROC fluencySetTempoPTR = NULL;
fluencySetVolumePROC fluencySetVolumePTR = NULL;
fluencySetBalancePROC fluencySetBalancePTR = NULL;
fluencySetParameterPROC fluencySetParameterPTR = NULL;
fluencySetParameterWPROC fluencySetParameterWPTR = NULL;
fluencySetInputTextPROC fluencySetInputTextPTR = NULL;
fluencySetInputTextWPROC fluencySetInputTextWPTR = NULL;
fluencyGetPhonePROC fluencyGetPhonePTR = NULL;
fluencyGetBookmarkPROC fluencyGetBookmarkPTR = NULL;
fluencySpeakPROC fluencySpeakPTR = NULL;
fluencyStopSpeakingPROC fluencyStopSpeakingPTR = NULL;
fluencyPauseResumeSpeakingPROC fluencyPauseResumeSpeakingPTR = NULL;
fluencySpeakToFilePROC fluencySpeakToFilePTR = NULL;
fluencySpeakToFileWPROC fluencySpeakToFileWPTR = NULL;
fluencyAbortSpeakToFilePROC fluencyAbortSpeakToFilePTR = NULL;
fluencyScanTextPROC fluencyScanTextPTR = NULL;
fluencyAbortScanTextPROC fluencyAbortScanTextPTR = NULL;

// lexicon
fluencyLookupWordPROC fluencyLookupWordPTR = NULL;
fluencyLookupWordWPROC fluencyLookupWordWPTR = NULL;
fluencyAddWordPROC fluencyAddWordPTR = NULL;
fluencyAddWordWPROC fluencyAddWordWPTR = NULL;
fluencyUserLexiconNextWordPROC fluencyUserLexiconNextWordPTR = NULL;
fluencyUserLexiconNextWordWPROC fluencyUserLexiconNextWordWPTR = NULL;
fluencyUserLexiconClearPROC fluencyUserLexiconClearPTR = NULL;
fluencyUserLexiconImportExportPROC fluencyUserLexiconImportPTR = NULL;
fluencyUserLexiconImportExportWPROC fluencyUserLexiconImportWPTR = NULL;
fluencyUserLexiconImportExportPROC fluencyUserLexiconExportPTR = NULL;
fluencyUserLexiconImportExportWPROC fluencyUserLexiconExportWPTR = NULL;

// settings
fluencyGetPreferredVoiceNamePROC fluencyGetPreferredVoiceNamePTR = NULL;
fluencyGetPreferredVoiceNameWPROC fluencyGetPreferredVoiceNameWPTR = NULL;
fluencySetPreferredVoiceNamePROC fluencySetPreferredVoiceNamePTR = NULL;
fluencySetPreferredVoiceNameWPROC fluencySetPreferredVoiceNameWPTR = NULL;
fluencyGetPreferredTempoPROC fluencyGetPreferredTempoPTR = NULL;
fluencySetPreferredTempoPROC fluencySetPreferredTempoPTR = NULL;
fluencyGetPreferredVolumePROC fluencyGetPreferredVolumePTR = NULL;
fluencySetPreferredVolumePROC fluencySetPreferredVolumePTR = NULL;
fluencyGetPreferredBalancePROC fluencyGetPreferredBalancePTR = NULL;
fluencySetPreferredBalancePROC fluencySetPreferredBalancePTR = NULL;
fluencyGetModePROC fluencyGetReadingModePTR = NULL;
fluencySetModePROC fluencySetReadingModePTR = NULL;
fluencyGetModePROC fluencyGetSpellOutModePTR = NULL;
fluencySetModePROC fluencySetSpellOutModePTR = NULL;
fluencyGetPunctuationModePROC fluencyGetPunctuationModePTR = NULL;
fluencySetPunctuationModePROC fluencySetPunctuationModePTR = NULL;
fluencySaveSettingsPROC fluencySaveSettingsPTR = NULL;
fluencyUpdateSettingsPROC fluencyUpdateSettingsPTR = NULL;
fluencyGetUserDataPathPROC fluencyGetUserDataPathPTR = NULL;
fluencyGetUserDataPathWPROC fluencyGetUserDataPathWPTR = NULL;
fluencySetFrisianModePROC fluencySetFrisianModePTR = NULL;

// load dll

HINSTANCE fluencyLoadDLL(wchar_t *TTSPath)
{

	HKEY Key;
	unsigned long MaxBytes = 500;
	wchar_t Path[500];
	HINSTANCE  hFluencyDLL;

	hFluencyDLL = 0;
	Path[0] = '\0';

	if (wcslen(TTSPath) == 0) { // normal case, get path from registry
		RegOpenKey(HKEY_LOCAL_MACHINE, L"Software\\Fluency\\TTS", &Key);
		RegQueryValueEx(Key, L"Path", NULL, NULL, (LPBYTE)&Path, &MaxBytes);
		RegCloseKey(Key);
#ifdef _WIN64
		// add subdirectory for 64-bit version of Fluency TTS
		if (wcslen(Path) > 0) wcscat_s(Path, L"\\x64");
#endif
		}
	else {
		wcscpy_s(Path, TTSPath);
	}
	if (wcslen(Path) > 0) {
		// add path to dll search path (so dependencies of FLUENCY.DLL are found)
		SetDllDirectory(Path);
		// load FLUENCY.DLL
		wcscat_s(Path, L"\\FLUENCY.DLL");
		hFluencyDLL = LoadLibrary(Path);
		if (hFluencyDLL) {
			// lookup API functions

			// version info
			fluencyGetMajorVersionPTR = (fluencyGetVersionPROC)GetProcAddress(hFluencyDLL, "fluencyGetMajorVersion");
			fluencyGetMinorVersionPTR = (fluencyGetVersionPROC)GetProcAddress(hFluencyDLL, "fluencyGetMinorVersion");
			fluencyAboutWindowPTR = (fluencyAboutWindowPROC)GetProcAddress(hFluencyDLL, "fluencyAboutWindow");

			// initialize/close
			fluencyUnlockPTR = (fluencyUnlockPROC)GetProcAddress(hFluencyDLL, "fluencyUnlock");
			fluencyUnlockWPTR = (fluencyUnlockWPROC)GetProcAddress(hFluencyDLL, "fluencyUnlockW");
			fluencyInitializePTR = (fluencyInitializePROC)GetProcAddress(hFluencyDLL, "fluencyInitialize");
			fluencyInitializeFromPTR = (fluencyInitializeFromPROC)GetProcAddress(hFluencyDLL, "fluencyInitializeFrom");
			fluencyInitializeFromWPTR = (fluencyInitializeFromWPROC)GetProcAddress(hFluencyDLL, "fluencyInitializeFromW");
			fluencyClosePTR = (fluencyClosePROC)GetProcAddress(hFluencyDLL, "fluencyClose");

			// voice info
			fluencyGetVoiceCountPTR = (fluencyGetVoiceCountPROC)GetProcAddress(hFluencyDLL, "fluencyGetVoiceCount");
			fluencyGetVoiceNamePTR = (fluencyGetVoiceNamePROC)GetProcAddress(hFluencyDLL, "fluencyGetVoiceName");
			fluencyGetVoiceNameWPTR = (fluencyGetVoiceNameWPROC)GetProcAddress(hFluencyDLL, "fluencyGetVoiceNameW");

			// voice object
			fluencyCreateVoicePTR = (fluencyCreateVoicePROC)GetProcAddress(hFluencyDLL, "fluencyCreateVoice");
			fluencyCreateVoiceWPTR = (fluencyCreateVoiceWPROC)GetProcAddress(hFluencyDLL, "fluencyCreateVoiceW");
			fluencyDeleteVoicePTR = (fluencyDeleteVoicePROC)GetProcAddress(hFluencyDLL, "fluencyDeleteVoice");

			// channel object
			fluencyCreateChannelPTR = (fluencyCreateChannelPROC)GetProcAddress(hFluencyDLL, "fluencyCreateChannel");
			fluencyDeleteChannelPTR = (fluencyDeleteChannelPROC)GetProcAddress(hFluencyDLL, "fluencyDeleteChannel");
			fluencySetVoicePTR = (fluencySetVoicePROC)GetProcAddress(hFluencyDLL, "fluencySetVoice");
			fluencySetTempoPTR = (fluencySetTempoPROC)GetProcAddress(hFluencyDLL, "fluencySetTempo");
			fluencySetVolumePTR = (fluencySetVolumePROC)GetProcAddress(hFluencyDLL, "fluencySetVolume");
			fluencySetBalancePTR = (fluencySetBalancePROC)GetProcAddress(hFluencyDLL, "fluencySetBalance");
			fluencySetParameterPTR = (fluencySetParameterPROC)GetProcAddress(hFluencyDLL, "fluencySetParameter");
			fluencySetParameterWPTR = (fluencySetParameterWPROC)GetProcAddress(hFluencyDLL, "fluencySetParameterW");
			fluencySetInputTextPTR = (fluencySetInputTextPROC)GetProcAddress(hFluencyDLL, "fluencySetInputText");
			fluencySetInputTextWPTR = (fluencySetInputTextWPROC)GetProcAddress(hFluencyDLL, "fluencySetInputTextW");
			fluencyGetPhonePTR = (fluencyGetPhonePROC)GetProcAddress(hFluencyDLL, "fluencyGetPhone");
			fluencyGetBookmarkPTR = (fluencyGetBookmarkPROC)GetProcAddress(hFluencyDLL, "fluencyGetBookmark");
			fluencySpeakPTR = (fluencySpeakPROC)GetProcAddress(hFluencyDLL, "fluencySpeak");
			fluencyStopSpeakingPTR = (fluencyStopSpeakingPROC)GetProcAddress(hFluencyDLL, "fluencyStopSpeaking");
			fluencyPauseResumeSpeakingPTR = (fluencyPauseResumeSpeakingPROC)GetProcAddress(hFluencyDLL, "fluencyPauseResumeSpeaking");
			fluencySpeakToFilePTR = (fluencySpeakToFilePROC)GetProcAddress(hFluencyDLL, "fluencySpeakToFile");
			fluencySpeakToFileWPTR = (fluencySpeakToFileWPROC)GetProcAddress(hFluencyDLL, "fluencySpeakToFileW");
			fluencyAbortSpeakToFilePTR = (fluencyAbortSpeakToFilePROC)GetProcAddress(hFluencyDLL, "fluencyAbortSpeakToFile");
			fluencyScanTextPTR = (fluencyScanTextPROC)GetProcAddress(hFluencyDLL, "fluencyScanText");
			fluencyAbortScanTextPTR = (fluencyAbortScanTextPROC)GetProcAddress(hFluencyDLL, "fluencyAbortScanText");

			// lexicon
			fluencyLookupWordPTR = (fluencyLookupWordPROC)GetProcAddress(hFluencyDLL, "fluencyLookupWord");
			fluencyLookupWordWPTR = (fluencyLookupWordWPROC)GetProcAddress(hFluencyDLL, "fluencyLookupWordW");
			fluencyAddWordPTR = (fluencyAddWordPROC)GetProcAddress(hFluencyDLL, "fluencyAddWord");
			fluencyAddWordWPTR = (fluencyAddWordWPROC)GetProcAddress(hFluencyDLL, "fluencyAddWordW");
			fluencyUserLexiconNextWordPTR = (fluencyUserLexiconNextWordPROC)GetProcAddress(hFluencyDLL, "fluencyUserLexiconNextWord");
			fluencyUserLexiconNextWordWPTR = (fluencyUserLexiconNextWordWPROC)GetProcAddress(hFluencyDLL, "fluencyUserLexiconNextWordW");
			fluencyUserLexiconClearPTR = (fluencyUserLexiconClearPROC)GetProcAddress(hFluencyDLL, "fluencyUserLexiconClear");
			fluencyUserLexiconExportPTR = (fluencyUserLexiconImportExportPROC)GetProcAddress(hFluencyDLL, "fluencyUserLexiconExport");
			fluencyUserLexiconExportWPTR = (fluencyUserLexiconImportExportWPROC)GetProcAddress(hFluencyDLL, "fluencyUserLexiconExportW");
			fluencyUserLexiconImportPTR = (fluencyUserLexiconImportExportPROC)GetProcAddress(hFluencyDLL, "fluencyUserLexiconImport");
			fluencyUserLexiconImportWPTR = (fluencyUserLexiconImportExportWPROC)GetProcAddress(hFluencyDLL, "fluencyUserLexiconImportW");

			// settings
			fluencyGetPreferredVoiceNamePTR = (fluencyGetPreferredVoiceNamePROC)GetProcAddress(hFluencyDLL, "fluencyGetPreferredVoiceName");
			fluencyGetPreferredVoiceNameWPTR = (fluencyGetPreferredVoiceNameWPROC)GetProcAddress(hFluencyDLL, "fluencyGetPreferredVoiceNameW");
			fluencySetPreferredVoiceNamePTR = (fluencySetPreferredVoiceNamePROC)GetProcAddress(hFluencyDLL, "fluencySetPreferredVoiceName");
			fluencySetPreferredVoiceNameWPTR = (fluencySetPreferredVoiceNameWPROC)GetProcAddress(hFluencyDLL, "fluencySetPreferredVoiceNameW");
			fluencyGetPreferredTempoPTR = (fluencyGetPreferredTempoPROC)GetProcAddress(hFluencyDLL, "fluencyGetPreferredTempo");
			fluencySetPreferredTempoPTR = (fluencySetPreferredTempoPROC)GetProcAddress(hFluencyDLL, "fluencySetPreferredTempo");
			fluencyGetPreferredVolumePTR = (fluencyGetPreferredVolumePROC)GetProcAddress(hFluencyDLL, "fluencyGetPreferredVolume");
			fluencySetPreferredVolumePTR = (fluencySetPreferredVolumePROC)GetProcAddress(hFluencyDLL, "fluencySetPreferredVolume");
			fluencyGetPreferredBalancePTR = (fluencyGetPreferredBalancePROC)GetProcAddress(hFluencyDLL, "fluencyGetPreferredBalance");
			fluencySetPreferredBalancePTR = (fluencySetPreferredBalancePROC)GetProcAddress(hFluencyDLL, "fluencySetPreferredBalance");
			fluencyGetReadingModePTR = (fluencyGetModePROC)GetProcAddress(hFluencyDLL, "fluencyGetReadingMode");
			fluencySetReadingModePTR = (fluencySetModePROC)GetProcAddress(hFluencyDLL, "fluencySetReadingMode");
			fluencyGetSpellOutModePTR = (fluencyGetModePROC)GetProcAddress(hFluencyDLL, "fluencyGetSpellOutMode");
			fluencySetSpellOutModePTR = (fluencySetModePROC)GetProcAddress(hFluencyDLL, "fluencySetSpellOutMode");
			fluencyGetPunctuationModePTR = (fluencyGetPunctuationModePROC)GetProcAddress(hFluencyDLL, "fluencyGetPunctuationMode");
			fluencySetPunctuationModePTR = (fluencySetPunctuationModePROC)GetProcAddress(hFluencyDLL, "fluencySetPunctuationMode");
			fluencySaveSettingsPTR = (fluencySaveSettingsPROC)GetProcAddress(hFluencyDLL, "fluencySaveSettings");
			fluencyUpdateSettingsPTR = (fluencyUpdateSettingsPROC)GetProcAddress(hFluencyDLL, "fluencyUpdateSettings");
			fluencyGetUserDataPathPTR = (fluencyGetUserDataPathPROC)GetProcAddress(hFluencyDLL, "fluencyGetUserDataPath");
			fluencyGetUserDataPathWPTR = (fluencyGetUserDataPathWPROC)GetProcAddress(hFluencyDLL, "fluencyGetUserDataPathW");
			fluencySetFrisianModePTR = (fluencySetFrisianModePROC)GetProcAddress(hFluencyDLL, "fluencySetFrisianMode");
		}
	}
	return hFluencyDLL;
}


// implementation of API functions

unsigned fluencyGetMajorVersion(void)
{
	if (fluencyGetMajorVersionPTR) 
		return fluencyGetMajorVersionPTR(); 
	else 
		return 0;
}

unsigned fluencyGetMinorVersion(void)
{
	if (fluencyGetMinorVersionPTR) 
		return fluencyGetMinorVersionPTR(); 
	else 
		return 0;
}

void fluencyAboutWindow(HWND Win)
{
	if (fluencyAboutWindowPTR) fluencyAboutWindowPTR(Win);
}

void fluencyUnlock(char *Key, char *Extra)
{
	if (fluencyUnlockPTR) fluencyUnlockPTR(Key, Extra);
}

void fluencyUnlockW(wchar_t *Key, wchar_t *Extra)
{
	if (fluencyUnlockWPTR) fluencyUnlockWPTR(Key, Extra);
}

bool fluencyInitialize(void)
{
	if (fluencyInitializePTR) 
		return fluencyInitializePTR(); 
	else 
		return FALSE;
}

bool fluencyInitializeFrom(char *TTSPath, char *UserPath)
{
	if (fluencyInitializeFromPTR)
		return fluencyInitializeFromPTR(TTSPath, UserPath);
	else
		return FALSE;
}

bool fluencyInitializeFromW(wchar_t *TTSPath, wchar_t *UserPath)
{
	if (fluencyInitializeFromWPTR)
		return fluencyInitializeFromWPTR(TTSPath, UserPath);
	else
		return FALSE;
}

void fluencyClose(void)
{
	if (fluencyClosePTR) fluencyClosePTR();
}

unsigned fluencyGetVoiceCount(void)
{
	if (fluencyGetVoiceCountPTR) 
		return fluencyGetVoiceCountPTR(); 
	else 
		return 0;
}

unsigned fluencyGetVoiceName(unsigned VoiceIndex, char *Name, unsigned MaxBytes)
{
	if (fluencyGetVoiceNamePTR)
		return fluencyGetVoiceNamePTR(VoiceIndex, Name, MaxBytes);
	else {
		Name[0] = '\0';
		return 0;
	}
}

unsigned fluencyGetVoiceNameW(unsigned VoiceIndex, wchar_t *Name, unsigned MaxBytes)
{
	if (fluencyGetVoiceNameWPTR)
		return fluencyGetVoiceNameWPTR(VoiceIndex, Name, MaxBytes);
	else {
		Name[0] = '\0';
		return 0;
	}
}

void *fluencyCreateVoice(char *Name)
{
	if (fluencyCreateVoicePTR)
		return fluencyCreateVoicePTR(Name);
	else
		return NULL;
}

void *fluencyCreateVoiceW(wchar_t *Name)
{
	if (fluencyCreateVoiceWPTR)
		return fluencyCreateVoiceWPTR(Name);
	else
		return NULL;
}

void fluencyDeleteVoice(void *Voice)
{
	if (fluencyDeleteVoicePTR) fluencyDeleteVoicePTR(Voice);
}

void *fluencyCreateChannel(void *Voice, unsigned SamplingRate, bool Stereo)
{
	if (fluencyCreateChannelPTR) 
		return fluencyCreateChannelPTR(Voice, SamplingRate, Stereo); 
	else 
		return NULL;
}

void fluencyDeleteChannel(void *Channel)
{
	if (fluencyDeleteChannelPTR) fluencyDeleteChannelPTR(Channel);
}

void fluencySetVoice(void *Channel, void *Voice)
{
	if (fluencySetVoicePTR) fluencySetVoicePTR(Channel, Voice);
}

void fluencySetTempo(void *Channel, int Tempo)
{
	if (fluencySetTempoPTR) fluencySetTempoPTR(Channel, Tempo);
}

void fluencySetVolume(void *Channel, unsigned Volume)
{
	if (fluencySetVolumePTR) fluencySetVolumePTR(Channel, Volume);
}

void fluencySetBalance(void *Channel, int Balance)
{
	if (fluencySetBalancePTR) fluencySetBalancePTR(Channel, Balance);
}

void fluencySetParameter(void *Channel, char *Name, char *Value)
{
	if (fluencySetParameterPTR) fluencySetParameterPTR(Channel, Name, Value);
}

void fluencySetParameterW(void *Channel, wchar_t *Name, wchar_t *Value)
{
	if (fluencySetParameterWPTR) fluencySetParameterWPTR(Channel, Name, Value);
}

void fluencySetInputText(void *Channel, char *Text)
{
	if (fluencySetInputTextPTR) fluencySetInputTextPTR(Channel, Text);
}

void fluencySetInputTextW(void *Channel, wchar_t *Text)
{
	if (fluencySetInputTextWPTR) fluencySetInputTextWPTR(Channel, Text);
}

bool fluencyGetPhone(void *Channel, char **Phone, unsigned *Samples, void **Wav, unsigned *TextIndex, unsigned *WordLength)
{
	if (fluencyGetPhonePTR) 
		return fluencyGetPhonePTR(Channel, Phone, Samples, Wav, TextIndex, WordLength); 
	else 
		return FALSE;
}

bool fluencyGetBookmark(void *Channel, unsigned *Bookmark)
{
	if (fluencyGetBookmarkPTR) 
		return fluencyGetBookmarkPTR(Channel, Bookmark); 
	else {
		Bookmark = 0;
		return FALSE;
	}
}

bool fluencySpeak(void *Channel, void *Sync, unsigned User)
{
	if (fluencySpeakPTR) 
		return fluencySpeakPTR(Channel, Sync, User); 
	else 
		return FALSE;
}

void fluencyStopSpeaking(void *Channel)
{
	if (fluencyStopSpeakingPTR) fluencyStopSpeakingPTR(Channel);
}

void fluencyPauseResumeSpeaking(void *Channel)
{
	if (fluencyPauseResumeSpeakingPTR) fluencyPauseResumeSpeakingPTR(Channel);
}

void fluencySpeakToFile(void *Channel, char *Filename, void *Sync, unsigned User)
{
	if (fluencySpeakToFilePTR) fluencySpeakToFilePTR(Channel, Filename, Sync, User);
}

void fluencySpeakToFileW(void *Channel, wchar_t *Filename, void *Sync, unsigned User)
{
	if (fluencySpeakToFileWPTR) fluencySpeakToFileWPTR(Channel, Filename, Sync, User);
}

void fluencyAbortSpeakToFile(void *Channel)
{
	if (fluencyAbortSpeakToFilePTR) fluencyAbortSpeakToFilePTR(Channel);
}

void fluencyScanText(void *Channel, unsigned Sensitivity, void *Sync, unsigned User)
{
	if (fluencyScanTextPTR) fluencyScanTextPTR(Channel, Sensitivity, Sync, User);
}

void fluencyAbortScanText(void *Channel)
{
	if (fluencyAbortScanTextPTR) fluencyAbortScanTextPTR(Channel);
}

unsigned fluencyLookupWord(char *Word, char *Transcription, unsigned MaxBytes)
{
	if (fluencyLookupWordPTR)
		return fluencyLookupWordPTR(Word, Transcription, MaxBytes);
	else
		return 0;
}

unsigned fluencyLookupWordW(wchar_t *Word, wchar_t *Transcription, unsigned MaxBytes)
{
	if (fluencyLookupWordWPTR)
		return fluencyLookupWordWPTR(Word, Transcription, MaxBytes);
	else
		return 0;
}

void fluencyAddWord(char *Word, char *Transcription)
{
	if (fluencyAddWordPTR) fluencyAddWordPTR(Word, Transcription);
}

void fluencyAddWordW(wchar_t *Word, wchar_t *Transcription)
{
	if (fluencyAddWordWPTR) fluencyAddWordWPTR(Word, Transcription);
}

unsigned fluencyUserLexiconNextWord(char *Word, char *NextWord, unsigned MaxBytes)
{
	if (fluencyUserLexiconNextWordPTR)
		return fluencyUserLexiconNextWordPTR(Word, NextWord, MaxBytes);
	else
		return 0;
}

unsigned fluencyUserLexiconNextWordW(wchar_t *Word, wchar_t *NextWord, unsigned MaxBytes)
{
	if (fluencyUserLexiconNextWordWPTR)
		return fluencyUserLexiconNextWordWPTR(Word, NextWord, MaxBytes);
	else
		return 0;
}

void fluencyUserLexiconClear(void)
{
	if (fluencyUserLexiconClearPTR) fluencyUserLexiconClearPTR();
}

void fluencyUserLexiconImport(char *Filename)
{
	if (fluencyUserLexiconImportPTR) fluencyUserLexiconImportPTR(Filename);
}

void fluencyUserLexiconImportW(wchar_t *Filename)
{
	if (fluencyUserLexiconImportWPTR) fluencyUserLexiconImportWPTR(Filename);
}

void fluencyUserLexiconExport(char *Filename)
{
	if (fluencyUserLexiconExportPTR) fluencyUserLexiconExportPTR(Filename);
}

void fluencyUserLexiconExportW(wchar_t *Filename)
{
	if (fluencyUserLexiconExportWPTR) fluencyUserLexiconExportWPTR(Filename);
}

unsigned fluencyGetPreferredVoiceName(char *Name, unsigned MaxBytes)
{
	if (fluencyGetPreferredVoiceNamePTR)
		return fluencyGetPreferredVoiceNamePTR(Name, MaxBytes);
	else
		return 0;
}

unsigned fluencyGetPreferredVoiceNameW(wchar_t *Name, unsigned MaxBytes)
{
	if (fluencyGetPreferredVoiceNameWPTR)
		return fluencyGetPreferredVoiceNameWPTR(Name, MaxBytes);
	else
		return 0;
}

void fluencySetPreferredVoiceName(char *Name)
{
	if (fluencySetPreferredVoiceNamePTR) fluencySetPreferredVoiceNamePTR(Name);
}

void fluencySetPreferredVoiceNameW(wchar_t *Name)
{
	if (fluencySetPreferredVoiceNameWPTR) fluencySetPreferredVoiceNameWPTR(Name);
}

int fluencyGetPreferredTempo(void)
{
	if (fluencyGetPreferredTempoPTR) 
		return fluencyGetPreferredTempoPTR(); 
	else 
		return 0;
}

void fluencySetPreferredTempo(int Tempo)
{
	if (fluencySetPreferredTempoPTR) fluencySetPreferredTempoPTR(Tempo);
}

unsigned fluencyGetPreferredVolume(void)
{
	if (fluencyGetPreferredVolumePTR) 
		return fluencyGetPreferredVolumePTR(); 
	else 
		return 0;
}

void fluencySetPreferredVolume(unsigned Volume)
{
	if (fluencySetPreferredVolumePTR) fluencySetPreferredVolumePTR(Volume);
}

int fluencyGetPreferredBalance(void)
{
	if (fluencyGetPreferredBalancePTR) 
		return fluencyGetPreferredBalancePTR(); 
	else 
		return 0;
}

void fluencySetPreferredBalance(int Balance)
{
	if (fluencySetPreferredBalancePTR) fluencySetPreferredBalancePTR(Balance);
}

unsigned fluencyGetReadingMode(void)
{
	if (fluencyGetReadingModePTR) 
		return fluencyGetReadingModePTR(); 
	else 
		return 0;
}

void fluencySetReadingMode(unsigned Mode)
{
	if (fluencySetReadingModePTR) fluencySetReadingModePTR(Mode);
}

unsigned fluencyGetSpellOutMode(void)
{
	if (fluencyGetSpellOutModePTR) 
		return fluencyGetSpellOutModePTR(); 
	else 
		return 0;
}

void fluencySetSpellOutMode(unsigned Mode)
{
	if (fluencySetSpellOutModePTR) fluencySetSpellOutModePTR(Mode);
}

bool fluencyGetPunctuationMode(void)
{
	if (fluencyGetPunctuationModePTR) 
		return fluencyGetPunctuationModePTR(); 
	else 
		return 0;
}

void fluencySetPunctuationMode(bool Mode)
{
	if (fluencySetPunctuationModePTR) fluencySetPunctuationModePTR(Mode);
}

void fluencySaveSettings(void)
{
	if (fluencySaveSettingsPTR) fluencySaveSettingsPTR();
}

void fluencyUpdateSettings(void)
{
	if (fluencyUpdateSettingsPTR) fluencyUpdateSettingsPTR();
}

unsigned fluencyGetUserDataPath(char *Path, unsigned MaxBytes)
{
	if (fluencyGetUserDataPathPTR)
		return fluencyGetUserDataPathPTR(Path, MaxBytes);
	else
		return 0;
}

unsigned fluencyGetUserDataPathW(wchar_t *Path, unsigned MaxBytes)
{
	if (fluencyGetUserDataPathWPTR)
		return fluencyGetUserDataPathWPTR(Path, MaxBytes);
	else
		return 0;
}

bool fluencySetFrisianMode(bool OnOff)
{
	if (fluencySetFrisianModePTR) 
		return fluencySetFrisianModePTR(OnOff); 
	else 
		return 0;
}
