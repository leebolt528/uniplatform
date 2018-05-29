/**
 * Created by lss on 2017/8/6.
 */
const browserSync = require( 'browser-sync' );

const tasks = require( '../config/tasks' );

module.exports = ( callback ) => {

  return browserSync.init( tasks.browserSync, callback );

};