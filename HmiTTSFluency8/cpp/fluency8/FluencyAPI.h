// FluencyAPI.h
//
// C/C++ wrapper for Fluency API
// Copyright (C) 2008-2015 Fluency, Amsterdam

// note: you also need to add FluencyAPI.cpp to your project!

// see www.fluency.nl/api for documentation

#include "Windows.h"

// sync events

#define SYNC_START		0
#define SYNC_PROGRESS		1
#define SYNC_FINISH		2
#define SYNC_PHONEME		3
#define SYNC_SCAN		4
#define SYNC_BOOKMARK		5
#define SYNC_DIPHONE_INDEX	6

// load FLUENCY.DLL

HINSTANCE fluencyLoadDLL(wchar_t *TTSPath);

// FLUENCY API

// version info - can be called before fluencyInitialize()
unsigned fluencyGetMajorVersion(void);
unsigned fluencyGetMinorVersion(void);
void fluencyAboutWindow(HWND Win);

// initialize/close
void fluencyUnlock(char *Key, char *Extra);
void fluencyUnlockW(wchar_t *Key, wchar_t *Extra);
bool fluencyInitialize(void);
bool fluencyInitializeFrom(char *TTSPath, char *UserPath);
bool fluencyInitializeFromW(wchar_t *TTSPath, wchar_t *UserPath);
void fluencyClose(void);

// voice info
unsigned fluencyGetVoiceCount(void);
unsigned fluencyGetVoiceName(unsigned VoiceIndex, char *NameBuffer, unsigned MaxBytes);
unsigned fluencyGetVoiceNameW(unsigned VoiceIndex, wchar_t *NameBuffer, unsigned MaxBytes);

// voice object
void *fluencyCreateVoice(char *Name);
void *fluencyCreateVoiceW(wchar_t *Name);
void fluencyDeleteVoice(void *Voice);

// channel object
void *fluencyCreateChannel(void *Voice, unsigned SamplingRate, bool Stereo);
void fluencyDeleteChannel(void *Channel);
void fluencySetVoice(void *Channel, void *Voice);
void fluencySetTempo(void *Channel, int Tempo);
void fluencySetVolume(void *Channel, unsigned Volume);
void fluencySetBalance(void *Channel, int Balance);
void fluencySetParameter(void *Channel, char *Name, char *Value);
void fluencySetParameterW(void *Channel, wchar_t *Name, wchar_t *Value);
void fluencySetInputText(void *Channel, char *Text);
void fluencySetInputTextW(void *Channel, wchar_t *Text);
bool fluencyGetPhone(void *Channel, char **Phone, unsigned *Samples, void **Wav, unsigned *TextIndex, unsigned *WordLength);
bool fluencyGetBookmark(void *Channel, unsigned *Bookmark);
bool fluencySpeak(void *Channel, void *Sync, unsigned User);
void fluencyStopSpeaking(void *Channel);
void fluencyPauseResumeSpeaking(void *Channel);
void fluencySpeakToFile(void *Channel, char *Filename, void *Sync, unsigned User);
void fluencySpeakToFileW(void *Channel, wchar_t *Filename, void *Sync, unsigned User);
void fluencyAbortSpeakToFile(void *Channel);
void fluencyScanText(void *Channel, unsigned Sensitivity, void *Sync, unsigned User);
void fluencyAbortScanText(void *Channel);

// lexicon
unsigned fluencyLookupWord(char *Word, char *Transcription, unsigned MaxBytes);
unsigned fluencyLookupWordW(wchar_t *Word, wchar_t *Transcription, unsigned MaxBytes);
void fluencyAddWord(char *Word, char *Transcription);
void fluencyAddWordW(wchar_t *Word, wchar_t *Transcription);
unsigned fluencyUserLexiconNextWord(char *Word, char *NextWord, unsigned MaxBytes);
unsigned fluencyUserLexiconNextWordW(wchar_t *Word, wchar_t *NextWord, unsigned MaxBytes);
// new lexicon functions in version 8.0
void fluencyUserLexiconClear(void);
void fluencyUserLexiconImport(char *Filename);
void fluencyUserLexiconImportW(wchar_t *Filename);
void fluencyUserLexiconExport(char *Filename);
void fluencyUserLexiconExportW(wchar_t *Filename);

// settings
unsigned fluencyGetPreferredVoiceName(char *Name, unsigned MaxBytes);
unsigned fluencyGetPreferredVoiceNameW(wchar_t *Name, unsigned MaxBytes);
void fluencySetPreferredVoiceName(char *Name);
void fluencySetPreferredVoiceNameW(wchar_t *Name);
int fluencyGetPreferredTempo(void);
void fluencySetPreferredTempo(int Tempo);
unsigned fluencyGetPreferredVolume(void);
void fluencySetPreferredVolume(unsigned Volume);
int fluencyGetPreferredBalance(void);
void fluencySetPreferredBalance(int Balance);
unsigned fluencyGetReadingMode(void);
void fluencySetReadingMode(unsigned Mode);
unsigned fluencyGetSpellOutMode(void);
void fluencySetSpellOutMode(unsigned Mode);
bool fluencyGetPunctuationMode(void);
void fluencySetPunctuationMode(bool Mode);
void fluencySaveSettings(void);
void fluencyUpdateSettings(void);
unsigned fluencyGetUserDataPath(char *Path, unsigned MaxBytes);
unsigned fluencyGetUserDataPathW(wchar_t *Path, unsigned MaxBytes);
bool fluencySetFrisianMode(bool OnOff);
