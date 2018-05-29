#!/bin/sh
########################################################
# The script is used to make bonc uniplatform package  #
#                                                      #
#                                                      #
########################################################


# PKG_BASE=/home/jenkins/workspace/uniplatform-build
currentpath=`pwd`
echo $currentpath

#clean package folder
/bin/rm -f $currentpath/../package/*

#copy jar
/bin/cp -r $currentpath/../uniplatform-web/uniplatform-portals-web/target/uniplatform.jar $currentpath/../package/uniplatform.jar



