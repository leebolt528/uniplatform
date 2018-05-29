/**
 * Created by lss on 2017/8/6.
 */
const gulp = require( 'gulp' );
const reunSequence = require( 'run-sequence' );

const nodemon = require( './develop/nodemon' );
const browserSync = require( './develop/browser-sync' );

gulp.task( 'nodemon', nodemon );
gulp.task( 'browser-sync', browserSync );

module.exports = ( callback ) => {

  console.log( 'Running dev server' );

  reunSequence( [ 'watch', 'nodemon', 'browser-sync' ], callback );

};