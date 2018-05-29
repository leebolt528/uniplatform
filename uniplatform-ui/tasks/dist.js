const runSequence  = require( 'run-sequence' );

module.exports = ( callback ) => {
  
  console.info( 'Distributing Uniplatfomr-ui' );
  
  runSequence( 'clean', [ 'build' ], callback );

};