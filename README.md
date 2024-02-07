# IPA

This compact CLI tool enriches words with their IPA transcriptions (American English), utilizing the [Cambridge Dictionary](https://dictionary.cambridge.org/us/dictionary/english) for transcription lookups.

Since Cambridge Dictionary doesn't have public API this tool fetches HTML page and parses out a transcription.

The Cambridge Dictionary is notably proficient in transcription for several reasons:
* uses the traditional, widely recognized IPA system rather than inventing a proprietary one.
* captures nuanced aspects of pronunciation, such as the flap T.
* its transcriptions lean towards the East Coast American accent (e.g., /dɑːɡ/ as opposed to /dɔg/). This choice is subjective, yet I've opted for the East Coast accent.

## Usage

1. Clone the repository and run `./gradlew installDist`
2. Run the tool using `./build/install/ipa/bin/ipa <path-to-input-file> <path-to-output-file>`
   * The input file should contain a list of English words, with each word on a separate line.
3. Import the output into Anki or spreadsheet of your choice. 

# License

   Copyright 2024 Andrii Lisun
   
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
       http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

