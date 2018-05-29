/**
 * Created by lss on 2017/8/7.
 */
const gulp    = require( 'gulp' );
const babel   = require( 'gulp-babel' );
const rename  = require( 'gulp-rename' );
const uglify  = require( 'gulp-uglify' );
const path    = require( 'path' );
const sourcemaps = require( 'gulp-sourcemaps' );
const header  = require( '../../libs/header' );

const config  = require( '../../config/settings' );
const tasks   = require( '../../config/tasks' );

const source  = config.paths.source.javascript;
const bowerPath = config.paths.source.bower;
const output  = config.paths.output.javascript;

module.exports = ( callback ) => {

  console.log( 'Building javascript usou' );

  return gulp.src( [
    path.join( source, '**', '*.js' ),
    `!${ path.join( bowerPath, '**', '*' ) }`,
    `!${ path.join( source, 'corpus', '**', '*' ) }`,
    `!${ path.join( source, 'dcci', '**', '*' ) }`,
    `!${ path.join( source, 'nlap', '**', '*' ) }`,
    `!${ path.join( source, 'portals', '**', '*' ) }`
  ] )
    .pipe( sourcemaps.init() )
    .pipe( babel( tasks.babel.javascript ) )
    .pipe( uglify( tasks.uglify ) )
    .pipe( header() )
    .pipe( rename( ( path ) => {
      path.basename += '.min';
      return path;
    } ) )
    .pipe( sourcemaps.write( './' ) )
    .pipe( gulp.dest( output ) );

};