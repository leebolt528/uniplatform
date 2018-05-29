const gulp        = require( 'gulp' );

// watch for file changes and build
const watch       = require('./tasks/watch');

// develop server
const develop = require( './tasks/develop' );

// build all files
const build       = require( './tasks/build' );

// utility tasks
const clean       = require( './tasks/clean' );

// distribution tasks
const dist        = require( './tasks/dist' );

const buildUsou   = require( './tasks/build-usou' );

/* Tasks */
gulp.task( 'default', [ 'build' ] );

gulp.task( 'build-usou', buildUsou );

gulp.task( 'watch', watch );

gulp.task( 'develop', develop );

gulp.task( 'build', build );

gulp.task( 'clean', clean );

gulp.task( 'dist', dist );