/**
 * Created by lss on 2017/8/7.
 */
const gulp    = require( 'gulp' );
const path    = require( 'path' );
const rename  = require( 'gulp-rename' );
const cleanCSS = require('gulp-clean-css');
const sourcemaps = require( 'gulp-sourcemaps' );
const header  = require( '../../libs/header' );

const config  = require( '../../config/settings' );
const tasks   = require( '../../config/tasks' );

const source  = config.paths.source.css;
const bowerPath = config.paths.source.bower;
const output  = config.paths.output.css;

module.exports = ( callback ) => {

  console.log( 'Building css usou' );

  return gulp.src( [
    path.join( source, '**', '*.css' ),
    `!${ path.join( bowerPath, '**', '*' ) }`,
    `!${ path.join( source, 'corpus', '**', '*' ) }`,
    `!${ path.join( source, 'dcci', '**', '*' ) }`,
    `!${ path.join( source, 'nlap', '**', '*' ) }`,
    `!${ path.join( source, 'portals', '**', '*' ) }`
  ] )
    .pipe( sourcemaps.init() )
    .pipe( cleanCSS() )
    .pipe( header() )
    .pipe( rename( ( path ) => {
      path.basename += '.min';
      return path;
    } ) )
    .pipe( sourcemaps.write( './' ) )
    .pipe( gulp.dest( output ) );

};