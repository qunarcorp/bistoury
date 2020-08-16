#!/usr/bin/env bash

cd ../..

PROJECT_DIR=$(pwd)
WEB_DIR=$PROJECT_DIR/src/web
WEB_APP_DIR=$PROJECT_DIR/src/main/webapp

cd $WEB_DIR

ng build --aot --prod

cd $WEB_APP_DIR

rm -rf assets 1-es5.*.js 1-es2015.*.js 1-es2015.*.js main-es*.js polyfills-es*.js runtime-es*.js

mv -vf $WEB_DIR/dist/assets $WEB_APP_DIR
mv -vf $WEB_DIR/dist/* $WEB_APP_DIR

sed 's/href="\/"/href=""/g' index.html>temp.html
sed 's/src="/src="\//g' temp.html>index.html

rm temp.html
