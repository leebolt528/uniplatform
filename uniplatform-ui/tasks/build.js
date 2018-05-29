const gulp         = require( 'gulp' );
const runSequence  = require( 'run-sequence' );

const buildComponents = require( './builds/components' );
const buildViews  = require( './builds/views' );
const buildFrame  = require( './builds/frame' );
const buildJavascript = require( './builds/javascript' );
const buildCss = require( './builds/css' );
const buildBower = require( './builds/bower' );
const buildImage = require( './builds/image' );
const buildResource = require( './builds/resource' );

gulp.task( 'build-frame', buildFrame );
gulp.task( 'build-views', buildViews );
gulp.task( 'build-components', buildComponents );
gulp.task( 'build-javascript', buildJavascript );
gulp.task( 'build-css', buildCss );
gulp.task( 'build-bower', buildBower );
gulp.task( 'build-image', buildImage );
gulp.task( 'build-resource', buildResource );

module.exports = ( callback ) => {

  console.info( 'Building' );

  let tasks = [];

  tasks.push( 'build-components' );
  tasks.push( 'build-views' );
  tasks.push( 'build-frame' );
  tasks.push( 'build-javascript' );
  tasks.push( 'build-css' );
  tasks.push( 'build-image' );
  tasks.push( 'build-bower' );
  tasks.push( 'build-resource' );

  runSequence( tasks, callback );

};
