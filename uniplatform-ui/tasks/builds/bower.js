/**
 * Created by lss on 2017/8/7.
 */
const gulp    = require( 'gulp' );
const path    = require( 'path' );

const config  = require( '../config/settings' );

const source  = config.paths.source.bower;
const output  = config.paths.output.bower;

module.exports = ( callback ) => {

  console.log( 'Building bower_components...' );

  return gulp.src( path.join( source, '**', '*' ) )
    .pipe( gulp.dest( output ) );

};