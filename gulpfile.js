process.env.DEBUG = 'coursework-thing';
require('babel-core'); // Just make sure we have our config up

const gulp = require('gulp');
const sass = require('gulp-sass');
const autoprefixer = require('autoprefixer');
const cssnano = require('cssnano');
const postcss = require('gulp-postcss');
const Builder = require('systemjs-builder');
const UglifyJS = require("uglify-js");
const watch = require('gulp-watch');
const YamlJS = require('yamljs');
const pug = require('pug');
const nodeSass = require('node-sass');
const debug = require('debug')('coursework-thing');

const Promise = require('bluebird');
const fs = Promise.promisifyAll(require('fs'));

const moduleConfig = {
    meta: {
        'main.js': {
            format: 'esm'
        },
        'bindings/**/*': {
            format: 'esm'
        },
        'map/**/*': {
            format: 'esm'
        },
        'lib/**/*': {
            format: 'esm'
        },
        'models/**/*': {
            format: 'esm'
        },
        'util/**/*': {
            format: 'esm'
        },
        'jquery-lib': {
            crossOrigin: true,
            scriptLoad: true,
            format: 'global',
            exports: '$'
        },
        jquery: {
            format: 'esm'
        },
        'knockout-lib': {
            crossOrigin: true,
            scriptLoad: true,
            format: 'global',
            exports: 'ko'
        },
        promise: {
            crossOrigin: true,
            scriptLoad: true,
            format: 'global',
            exports: 'Promise'
        },
        knockout: {
            format: 'esm'
        },
        'jquery-toast': {
            format: 'global',
            deps: ['jquery']
        }
    },
    map: {
        jquery: 'setup/jquery.js',
        knockout: 'setup/knockout.js',
        'jquery-toast': 'lib/toast/jquery.toast.min.js',
        text: 'text.js',
        template: 'util/template.js',
        'path': 'path.js'
    }
};

const loadYaml = path => {
    return new Promise((resolve, reject) => {
        debug(`Loading YAML from ${path}`);
        YamlJS.load(path, resolve);
    });
};

const renderSass = file => {
    return new Promise((resolve, reject) => {
        debug(`Rendering sass from ${file}`);
        nodeSass.render({outputStyle: 'compressed', file}, (err, res) => {
            if (err) reject(err);
            else resolve(res.css.toString());
        });
    });
};

const renderAPIDoc = () => {
    debug('Rendering API docs...');
    const yaml = loadYaml('./api-doc.yml').then(contents => {
        let out = {};

        for (let k of Object.keys(contents).sort()) {
            out[k] = contents[k];
            out[k].endpoints = out[k].endpoints.sort((a, b) => {
                a = [a.url, a.method || contents[k].method || ""].join("|");
                b = [b.url, b.method || contents[k].method || ""].join("|");
                if (a < b) return -1;
                if (a > b) return 1;
                return 0;
            });
        }

        return out;
    });
    return Promise.all([
        renderSass('./raw-assets/bootstrap-sass-3.3.7/stylesheets/bootstrap-custom.scss'),
        fs.readFileAsync('./api-doc.pug', 'utf8'),
        yaml
    ])
        .spread((css, pugfile, ymlSpec) => pug.render(pugfile, {ymlSpec, css}))
        .then(pug => fs.writeFileAsync('./api-doc.html', pug, 'utf8'));
};

const buildJS = () => {
    debug('Building JS');
    const builder = new Builder('./raw-assets/js');
    builder.config(moduleConfig);
    return builder.buildStatic('main.js', {
        runtime: true,
        minify: false,
        uglify: false,
        externals: ['jquery-lib', 'knockout-lib', 'promise'],
        globalDeps: {
            'jquery-lib': '$',
            'knockout-lib': 'ko',
            'promise': 'Promise'
        }
    }).then(out => {
        debug('JS built');
        return out.source;
    });
};

const writeJS = js => {
    return fs.writeFileAsync('./WebContent/static/out.js', js, 'utf8');
};

const wrapJS = js => {
    const wrappedVars = [
        'Object',
        'Error',
        'encodeURIComponent',
        'window'
    ].join(",");
    const start = `(function(${wrappedVars}){`;
    const end = `})(${wrappedVars})`;

    return start + js + end;
};

const uglifyJS = js => {
    debug('Uglifying JS');
    return Promise.resolve(UglifyJS.minify(js, {
        fromString: true,
        unsafe_math: true,
        unsafe_proto: true,
        conditionals: true,
        properties: true,
        comparisons: true,
        booleans: true,
        loops: true,
        if_return: true,
        join_vars: true,
        cascade: true,
        collapse_vars: true,
        reduce_vars: true,
        pure_getters: true
    })).then(o => {
        debug('JS uglified');
        return o.code;
    });
};

const sassy = stream => {
    debug('Building SASS');
    return stream
        .pipe(sass({outputStyle: 'compressed'}).on('error', sass.logError))
        .pipe(postcss([
            autoprefixer({browsers: ['last 15 versions']}),
            cssnano()
        ]))
        .pipe(gulp.dest('./WebContent/static'))
};

gulp.task('sass', () => {
    return sassy(gulp.src('./raw-assets/site-css.scss'));
});

gulp.task('js:dev', () => buildJS().then(writeJS));

gulp.task('js:live', () => {
    return buildJS().then(wrapJS).then(uglifyJS).then(writeJS);
});

const watchSass = () => {
    return sassy(watch('./raw-assets/site-css.scss'));
};

const watchJS = () => {
    let build = Promise.resolve();
    return watch('./raw-assets/js/**/*.js', {}, () => {
        build = build.then(buildJS).then(writeJS);
    });
};

gulp.task('watch:apidoc', () => {
    let render = Promise.resolve();

    return watch(['./api-doc.pug', './api-doc.yml'], {}, () => {
        render = render.then(renderAPIDoc).catch(console.error);
    });
});

gulp.task('watch:sass', watchSass);
gulp.task('watch:js', watchJS);

gulp.task('watch', ['watch:sass', 'watch:js', 'watch:apidoc']);
gulp.task('render:apidoc', renderAPIDoc);
gulp.task('default', ['js:live', 'sass', 'render:apidoc']);
