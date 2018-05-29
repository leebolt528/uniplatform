/**
 * Created by lss on 2017/8/7.
 */
const gulp    = require( 'gulp' );
const path    = require( 'path' );

const config  = require( '../config/settings' );

const source  = config.paths.source.resources;
const output  = config.paths.output.resources;

module.exports = ( callback ) => {

  console.log( 'Building resources...' );

  return gulp.src( path.join( source, '**', 'resources', '**', '*' ) )
    .pipe( gulp.dest( output ) );

};
