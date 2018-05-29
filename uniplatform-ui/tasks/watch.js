const gulp        = require( 'gulp' );
const path        = require( 'path' );
const browserSync = require( 'browser-sync' );

const config = require( './config/settings' );

const source = config.paths.source;

module.exports = ( callback ) => {
  
  console.log( 'Watching jsx files for changes' );
  
  /* Watch Frame */
  gulp.watch( [
    path.join( source.frame, '**', '*.jsx' ),
    path.join( source.frame, '**', '*.json' )
  ], [ 'build-frame' ]).on( 'change', ( event ) => {
    console.log( 'File ' + event.path + ' was ' + event.type + ', running tasks...' );
    browserSync.reload();
  } );
  
  /* Watch Views */
  gulp.watch( [
    path.join( source.views, '**', '*.jsx' )
  ], [ 'build-views' ]).on( 'change', ( event ) => {
    console.log( 'File ' + event.path + ' was ' + event.type + ', running tasks...' );
    browserSync.reload();
  } );

  // Watch custom components
  gulp.watch( [
    path.join( source.components, '**', '*.jsx' ),
    path.join( source.components, '**', '*.js' )
  ], [ 'build-components' ] ).on( 'change', (event) => {
    console.log( 'File ' + event.path + ' was ' + event.type + ', running tasks...' );
    browserSync.reload();
  } );

  // Watch public/js
  gulp.watch( [
    path.join( source.javascript, '**', '*.js' ), `!${ path.join( source.bower, '**', '*' ) }`
  ], [ 'build-javascript' ]).on( 'change', ( event ) => {
    console.log( 'File ' + event.path + ' was ' + event.type + ', running tasks...' );
  } );

  // watch public/css
  gulp.watch( [
    path.join( source.css, '**', '*.css' ), `!${ path.join( source.bower, '**', '*' ) }`
  ], [ 'build-css' ]).on( 'change', ( event ) => {
    console.log( 'File ' + event.path + ' was ' + event.type + ', running tasks...' );
  } );

  // Watch public/image
  gulp.watch( [
    path.join( source.image, '**', 'image', '*' ), `!${ path.join( source.bower, '**', '*' ) }`
  ], [ 'build-image' ]).on( 'change', ( event ) => {
    console.log( 'File ' + event.path + ' was ' + event.type + ', running tasks...' );
  } );

};