const config = require( './settings' );

const bsProxyPort = process.env.PORT || '9106';
const bsPort = parseInt( bsProxyPort, 10 ) + 1;
const bsUIPort = bsPort + 1;

module.exports = {

  /* Remove Files in Clean */
  del: {
    silent : true
  },
  
  /* Minified JS Settings */
  uglify: {
    mangle           : true,
    preserveComments : false
  },
  
  babel: {
    frame: {
      presets: [ 'latest', 'react' ]
    },
    
    views: {
      presets: [ 'latest', 'react' ],
      plugins: [
        [ 'transform-es2015-modules-umd', {
          'globals': {
            'es6-promise': 'Promise',
            'react': 'React',
            'react-dom': 'ReactDOM',
            'epm-ui': 'EPMUI',
            'uniplatform-ui': 'UniplatformUI'
          }
        } ]
      ],
      moduleId: 'UniplatformPage'
    },

    javascript: {
      presets: [ 'latest', 'es2015' ]
    }
  },

  webpack: {
    output: {
      library       : 'UniplatformUI',
      libraryTarget : 'umd'
    },
    externals: {
      react: {
        root      : 'React',
        commonjs2 : 'react',
        commonjs  : 'react',
        amd       : 'react'
      },
      'react-dom': {
        root: 'ReactDOM',
        commonjs2: 'react-dom',
        commonjs: 'react-dom',
        amd: 'react-dom'
      },
      'epm-ui': {
        root: 'EPMUI',
        commonjs2: 'epm-ui',
        commonjs: 'epm-ui',
        amd: 'epm-ui'
      }
    },
    resolve: {
      extensions: [ '', '.js', '.jsx' ]
    },
    module: {
      loaders: [
        {
          test    : /\.js|\.jsx$/,
          exclude : /node_modules/,
          loader  : 'babel',
          query: {
            presets: [ 'latest', 'react' ],
            plugins: [
              [ 'transform-es2015-modules-umd', {
                'globals': {
                  'es6-promise': 'Promise',
                  'react': 'React',
                  'react-dom': 'ReactDOM',
                  'epm-ui': 'EPMUI',
                  'uniplatform-ui': 'UniplatformUI'
                }
              } ]
            ]
          }
        }
      ],
      postLoaders: [
        { test: /\.js$/, loader: 'es3ify' }
      ]
    }
  },

  browserSync: {
    proxy: 'http://localhost:' + bsProxyPort,
    port: bsPort,
    ui: {
      port: bsUIPort
    }
  },

  nodemon: {
    script: 'bin/www',
    'ignore': [
      config.paths.output.frame + '*',
      config.paths.output.views + '*',
      config.paths.output.components + '*',
      'public/*',
      'tasks/*',
      'gulpfile.js'
    ]
  },

  imagemin: {
    gifsicle: { interlaced: true },
    jpegtran: { progressive: true },
    optipng: { optimizationLevel: 5 },
    svgo: {
      plugins: [ { removeViewBox: true } ]
    }
  }

};