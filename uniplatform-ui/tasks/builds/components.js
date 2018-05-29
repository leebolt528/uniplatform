const gulp    = require( 'gulp' );
const plumber = require( 'gulp-plumber' );
const webpack = require( 'webpack' );
const webpackStream = require( 'webpack-stream' );
const rename  = require( 'gulp-rename' );
const uglify  = require( 'gulp-uglify' );
const path = require( 'path' );
const header  = require( '../libs/header' );

const config  = require( '../config/settings' );
const tasks   = require( '../config/tasks' );

const source  = config.paths.source.components;
const output  = config.paths.output.components;
const names   = config.paths.filenames;

module.exports = ( callback ) => {

  console.info( 'Building components' );

  return gulp.src( path.join( source, 'foo.js' ) )
    .pipe( plumber() )
    .pipe( webpackStream( tasks.webpack, webpack ) )
    .pipe( header() )
    .pipe( rename( names.umdComJS ) )
    .pipe( gulp.dest( output ) )
    .pipe( uglify( tasks.uglify ) )
    .pipe( header() )
    .pipe( rename( names.minifiedComUMDJS ) )
    .pipe( gulp.dest( output ) );

};