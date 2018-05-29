const path  = require( 'path' );
const compile = require( path.join( process.cwd(), 'config', 'compile.json' ) );

module.exports = {
  
  banner: [
    '/*!',
    ' * ${pkg.name} - ${pkg.description}',
    ' * @version v${pkg.version} - ${time}',
    ' * @link ${pkg.homepage}',
    ' * Copyright (C) 2016 BONC All rights reserved.',
    ' */',
    ''
  ].join('\n'),
  
  paths: Object.assign( {}, compile, {
    clean: 'build/'
  } )
  
};