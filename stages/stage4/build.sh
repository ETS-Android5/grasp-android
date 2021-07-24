#!/usr/bin/env sh
# Copyright 2019 (c) all rights reserved
# by BuildAPKs https://buildapks.github.io/buildAPKs/
# Contributeur : https://github.com/HemanthJabalpuri
# Invocation : $HOME/buildAPKs/scripts/sh/build/build.sh
#####################################################################
set -e
[ -z "${RDR:-}" ] && RDR=".." # "$HOME/buildAPKs"
for CMD in aapt apksigner dx ecj
do
       	[ -z "$(command -v "$CMD")" ] && printf "%s\\n" " \"$CMD\" not found" && NOTFOUND=1
done
[ "$NOTFOUND" = "1" ] && exit
[ "$1" ] && [ -f "$1/AndroidManifest.xml" ] && cd "$1"
[ -f AndroidManifest.xml ] || exit

_CLEANUP_() {
       	printf "\\n\\n%s\\n" "Completing tasks..."
       	[ "$CLEAN" = "1" ] && mv "bin/$PKGNAME.apk" .
      	rmdir assets 2>/dev/null ||:
       	rmdir res 2>/dev/null ||:
       	rm -rf bin
       	rm -rf gen
       	rm -rf obj
	printf "\\n\\n%s\\n\\n" "Share https://wiki.termux.com/wiki/Development everwhere🌎🌍🌏🌐!"
}

_UNTP_() {
       	printf "\\n\\n%s\\n\\n\\n""Unable to process"
       	_CLEANUP_
       	exit
}

PKGNAME="$(grep -o "package=.*" AndroidManifest.xml | cut -d\" -f2)"


printf "%s\\n" "Beginning build"
[ -d assets ] || mkdir assets
[ -d res ] || mkdir res
mkdir -p bin
mkdir -p gen
mkdir -p obj


printf "%s\\n" "aapt: started..."
aapt package -f -m \
       	-M "AndroidManifest.xml" \
       	-J "gen" \
       	-S "res" || _UNTP_
printf "%s\\n\\n" "aapt: done"


printf "%s\\n" "ecj: begun..."
for JAVAFILE in $(find ./src/ -type f -name "*.java")
do
       	JAVAFILES="$JAVAFILES $JAVAFILE"
done

for JARFILE in $(find ./lib/ -type f -name "*.jar")
do
    CLASSFILES="$CLASSFILES -classpath $JARFILE"
    JARFILES="$JARFILES $JARFILE"
done



ecj -d obj -sourcepath . $JAVAFILES $CLASSFILES || _UNTP_
printf "%s\\n\\n" "ecj: done"


printf "%s\\n" "dx: started..."
dx --dex --output=bin/classes.dex obj $JARFILES || _UNTP_
printf "%s\\n\\n" "dx: done"


printf "%s\\n" "Making $PKGNAME.apk..."
aapt package -f \
       	--min-sdk-version 1 \
       	--target-sdk-version 23 \
       	-M AndroidManifest.xml \
       	-S res \
       	-A assets \
       	-F bin/"$PKGNAME.apk" || _UNTP_


printf "\n%s\\n" "Adding classes.dex to $PKGNAME.apk..."
cd bin || _UNTP_
aapt add -f "$PKGNAME.apk" classes.dex || { cd ..; _UNTP_; }

printf "\n%s" "Signing $PKGNAME.apk: "
apksigner sign --cert "$RDR/opt/key/certificate.pem" --key "$RDR/opt/key/key.pk8" "$PKGNAME.apk" || { cd ..; _UNTP_; }
printf "%s\\n" "DONE"
printf "%s" "Verifying $PKGNAME.apk: "
apksigner verify --verbose "$PKGNAME.apk" || { cd ..; _UNTP_; }
printf "%s\\n" "DONE"

if [ -d "$HOME/storage/downloads" ];
then
    echo "Copying $PKGNAME.apk to $HOME/storage/downloads/GRASP/"
    mkdir -p "$HOME/storage/downloads/GRASP"
    cp "$PKGNAME.apk" "$HOME/storage/downloads/GRASP/"
fi

cd ..

CLEAN=1
_CLEANUP_
# build.sh EOF
