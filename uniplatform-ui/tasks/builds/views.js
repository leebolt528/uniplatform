const gulp    = require( 'gulp' );
const plumber = require( 'gulp-plumber' );
const cached  = require( 'gulp-cached' );
const print   = require( 'gulp-print' );
const babel   = require( 'gulp-babel' );
const rename  = require( 'gulp-rename' );
const uglify  = require( 'gulp-uglify' );
const path    = require( 'path' );
const header  = require( '../libs/header' );

const config  = require( '../config/settings' );
const tasks   = require( '../config/tasks' );

const source  = config.paths.source.views;
const output  = config.paths.output.views;

module.exports = ( callback ) => {

  console.info( 'Building views' );
  
  return gulp.src( path.join( source, '**/*.jsx' ) )
    .pipe( plumber() )
    .pipe( cached( 'build-views' ) )
    .pipe( print() )
    .pipe( babel( tasks.babel.views ) )
    .pipe( header() )
    .pipe( gulp.dest( output ) )
    .pipe( uglify( tasks.uglify ) )
    .pipe( header() )
    .pipe( rename( ( path ) => {
      path.basename += '.min';
      return path;
    } ) )
    .pipe( gulp.dest( output ) );

};