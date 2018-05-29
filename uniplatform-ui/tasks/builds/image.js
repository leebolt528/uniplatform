/**
 * Created by lss on 2017/8/7.
 */
const gulp    = require( 'gulp' );
const path    = require( 'path' );
const imagemin = require( 'gulp-imagemin' );

const config  = require( '../config/settings' );
const tasks   = require( '../config/tasks' );

const source  = config.paths.source.image;
const bowerPath = config.paths.source.bower;
const output  = config.paths.output.image;

module.exports = ( callback ) => {

  console.log( 'Building image...' );

  return gulp.src( [ path.join( source, '**', 'image', '*' ), `!${ path.join( bowerPath, '**', '*' ) }` ] )
    .pipe( imagemin([
      imagemin.gifsicle( tasks.imagemin.gifsicle ),
      imagemin.jpegtran( tasks.imagemin.jpegtran ),
      imagemin.optipng( tasks.imagemin.optipng ),
      imagemin.svgo( tasks.imagemin.svgo )
    ]) )
    .pipe( gulp.dest( output ) );

};