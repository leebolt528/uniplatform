const gulp         = require( 'gulp' );
const runSequence  = require( 'run-sequence' );

const buildComponents = require( './builds/components' );
const buildViewsUsou  = require( './builds/build-usou/views-usou' );
const buildFrame  = require( './builds/frame' );
const buildJavascriptUsou = require( './builds/build-usou/javascript-usou' );
const buildCssUsou = require( './builds/build-usou/css-usou' );
const buildBower = require( './builds/bower' );
const buildImageUsou = require( './builds/build-usou/image-usou' );

gulp.task( 'build-frame', buildFrame );
gulp.task( 'build-views-usou', buildViewsUsou );
gulp.task( 'build-components', buildComponents );
gulp.task( 'build-javascript-usou', buildJavascriptUsou );
gulp.task( 'build-css-usou', buildCssUsou );
gulp.task( 'build-bower', buildBower );
gulp.task( 'build-image-usou', buildImageUsou );

module.exports = ( callback ) => {

  console.info( 'Building' );

  let tasks = [];

  tasks.push( 'build-components' );
  tasks.push( 'build-views-usou' );
  tasks.push( 'build-frame' );
  tasks.push( 'build-javascript-usou' );
  tasks.push( 'build-css-usou' );
  tasks.push( 'build-image-usou' );
  tasks.push( 'build-bower' );

  runSequence( tasks, callback );

};