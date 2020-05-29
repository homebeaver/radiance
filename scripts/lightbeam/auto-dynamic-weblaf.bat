::
::  Copyright (c) 2005-2020 Radiance Kirill Grouchnikov. All Rights Reserved.
::
::  Redistribution and use in source and binary forms, with or without
::  modification, are permitted provided that the following conditions are met:
::
::   o Redistributions of source code must retain the above copyright notice,
::     this list of conditions and the following disclaimer.
::
::   o Redistributions in binary form must reproduce the above copyright notice,
::     this list of conditions and the following disclaimer in the documentation
::     and/or other materials provided with the distribution.
::
::   o Neither the name of the copyright holder nor the names of
::     its contributors may be used to endorse or promote products derived
::     from this software without specific prior written permission.
::
::  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
::  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
::  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
::  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
::  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
::  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
::  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
::  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
::  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
::  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
::  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

set RADIANCE_VERSION=3.0-SNAPSHOT
set CLASSPATH=../../drop/%RADIANCE_VERSION%/tools/radiance-lightbeam-%RADIANCE_VERSION%.jar;../../build/libs-tools/jgoodies-forms-1.9.0.jar;../../build/libs-tools/jgoodies-common-1.8.1.jar
set WEBLAF_CLASSPATH=../../../thirdparty/weblaf-core-1.3.0-SNAPSHOT.jar;../../../thirdparty/weblaf-plugin-1.3.0-SNAPSHOT.jar;../../../thirdparty/weblaf-ui-1.3.0-SNAPSHOT.jar;../../../thirdparty/weblaf-deps/*

"%JAVA_HOME%"\bin\java -Dswing.defaultlaf=com.alee.laf.WebLookAndFeel -cp %CLASSPATH%;%WEBLAF_CLASSPATH% org.pushingpixels.lightbeam.DynamicPerformanceSuite 10