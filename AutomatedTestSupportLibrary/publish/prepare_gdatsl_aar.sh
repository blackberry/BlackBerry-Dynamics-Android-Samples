#!/bin/bash
# (c) 2017 BlackBerry Limited. All rights reserved.

set -e

SDK_BUILD_DIR=build
AAR_BUILD_DIR=../../aar_build
AAR_DIST_DIR=../../aar_dist
AAR_OUTPUT_PATH=build/outputs/aar
BUILD_TYPE="Debug"
LOCAL_PUBLISH=true
LOCAL_PATH=../../aar_publish/
FRAMEWORK_VERSION="1.0.0.0_test"

while getopts ":v:p:d" OPTION
do
    case $OPTION in
        d)  BUILD_TYPE="Debug"
            ;;
        :)  if [ "$OPTARG" == "v" ]
            then
                echo "Option -$OPTARG requires Framework Version number"
            elif  [ "$OPTARG" == "p" ]
            then
                echo "Option -$OPTARG full local path to publsih aar files"
            fi
            exit 0
        ;;
        v)  echo "Version  = $OPTARG"
            FRAMEWORK_VERSION="$OPTARG"
        ;;
        p)  echo "Publish local"
            LOCAL_PATH="$OPTARG"
            LOCAL_PUBLISH=true
        ;;
        ?)  echo "Use -d option to build in Debug mode. Release mode by default"
            echo "Use -p option to publish local AAR artefacts"
            exit 1
        ;;
    esac
done

#Configuration. Clean previous build.
rm -rf "${AAR_BUILD_DIR}"
mkdir -p "${AAR_BUILD_DIR}"

rm -rf "${AAR_DIST_DIR}"
mkdir -p "${AAR_DIST_DIR}"
GRADLE_LOC=.

#function to generate AAR files
prepareAARfile() {
    SOURCE_PATH="$1"
    if [ -z "$SOURCE_PATH" ];
    then
        echo "Error: Empty argument SOURCE_PATH"
        exit 1
    fi
    AAR_FILE_NAME="$2"
    if [ -z "$AAR_FILE_NAME" ];
    then
        echo "Error: Empty argument AAR_FILE_NAME"
        exit 1
    fi

    echo "*** Building ${SOURCE_PATH}"
	AAR_TEMP_BUILD_DIR="${AAR_BUILD_DIR}/tmp"
	cp -Rf "${SOURCE_PATH}" "${AAR_TEMP_BUILD_DIR}"
    
	pushd .
	cd "${AAR_TEMP_BUILD_DIR}"

    gradleOption="clean assemble${BUILD_TYPE}"
    echo "*** Building project with gradle build options = ${gradleOption}"
	"${GRADLE_LOC}/gradlew" $gradleOption

	popd

    #Copy AAR artefact to arr_dist fodler.
	cp "${AAR_TEMP_BUILD_DIR}/${AAR_OUTPUT_PATH}"/*.aar "${AAR_DIST_DIR}/${AAR_FILE_NAME}"
	rm -rf "${AAR_TEMP_BUILD_DIR}"
}

#Build handheld/gd
prepareAARfile ".." \
			   atsl.aar \

echo
echo "*** DONE WITH BUILDING AAR FILES"

# Publish these artefacts to the local directory, so we have all signature objects, pom files and correct versions
if $LOCAL_PUBLISH
then
    echo "*** Publish local AAR artefacts"
    
    gradleOption="-b ./publish_aar_artefacts.gradle publish"
    
    #sdk dir
    CURRENT_DIR=$(pwd)
    ARTIFACT_PATH="$CURRENT_DIR/$AAR_DIST_DIR"
    
    
    ../gradlew $gradleOption -DLOCAL_URL=${LOCAL_PATH} \
                             -DGROUP_ID=com.blackberry.blackberrydynamics \
                             -DVERSION_ID=${FRAMEWORK_VERSION} \
                             -DARTIFACT_DIR=${ARTIFACT_PATH}/ \
                             -DARTIFACT_ATSL_ID=atsl \

else
    echo "*** Publish Globally AAR artefacts"

    gradleOption="-b ./publish_aar_artefacts.gradle publish"

    #sdk dir
    CURRENT_DIR=$(pwd)
    ARTIFACT_PATH="$CURRENT_DIR/$AAR_DIST_DIR"


    ../gradlew $gradleOption -DGROUP_ID=com.blackberry.blackberrydynamics \
                             -DVERSION_ID=${FRAMEWORK_VERSION} \
                             -DARTIFACT_DIR=${ARTIFACT_PATH}/ \
                             -DARTIFACT_ATSL_ID=atsl \

fi

