const gulp    = require( 'gulp' );
const babel   = require( 'gulp-babel' );
const merge   = require( 'merge-stream' );
const path    = require( 'path' );
const header  = require( '../libs/header' );

const config  = require( '../config/settings' );
const tasks   = require( '../config/tasks' );

const source  = config.paths.source.frame;
const output  = config.paths.output.frame;

module.exports = ( callback ) => {

  console.info( 'Building frame' );
  
  const jsxStream = gulp.src( path.join( source, '**/*.jsx' ) )
    .pipe( babel( tasks.babel.frame ) )
    .pipe( header() );
  
  const cfgStream = gulp.src( path.join( source, '**/*.json' ) );
  
  return merge( [ jsxStream, cfgStream ] )
    .pipe( gulp.dest( output ) );

};