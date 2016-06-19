var gulp = require('gulp'),
    concat = require('gulp-concat'),
    ngAnnotate = require('gulp-ng-annotate'),
    gulpIf = require('gulp-if'),
    argument = require('yargs').argv,
    uglify = require('gulp-uglify'),
    minifyCss = require('gulp-minify-css'),
    base64 = require('gulp-base64'),
    less = require('gulp-less'),
    path = require('path'),
    flatten = require('gulp-flatten');

var isProduction = argument.env === 'production';

var cssFiles = ['public/css/bootstrap/custom.css', 'public/css/app.css', 'public/css/photoswipe.css', 'public/css/default-skin.css', 'public/output/partials/**/*.css'];
var lessFiles = ['public/less/variables/*.less', 'public/less/pages/*.less', 'public/less/*.less', 'public/less/pages/partials/*.less'];
var jsFiles = [
    'public/web/*.js',
    'public/web/vendor/*.js',
    'public/web/shared/*.js',
    'public/web/location/*.js',
    'public/web/survey/*.js',
    'public/web/header/*.js',
    'public/web/contact/*.js',
    'public/web/booking/*.js',
    'public/web/apartments/*.js',
    'public/web/home/*.js',
    'public/web/surroundings/*.js',
    'public/web/boat/*.js'
];

function concatScripts() {
    gulp.src(jsFiles)
        .pipe(concat('app.js'))
        .pipe(gulpIf(isProduction, ngAnnotate()))
        .pipe(gulpIf(isProduction, uglify()))
        .pipe(gulp.dest('public/output/'));
}

function concatCssFiles() {
    gulp.src(cssFiles)
        .pipe(concat('app.css'))
        .pipe(gulpIf(isProduction, base64()))
        .pipe(gulpIf(isProduction, minifyCss()))
        .pipe(gulp.dest('public/output/'));
}

function compileLssFiles() {
    gulp.src('public/less/style.less')
        .pipe(less({
            paths: [ path.join(__dirname, 'less') ]
        }))
        .pipe(gulp.dest('./public/output/partials'));
}

gulp.task('less', function () {
    compileLssFiles();
});

gulp.task('css', function () {
    concatCssFiles();
});

gulp.task('js', function () {
    concatScripts();
});

gulp.task('watch', function () {
    gulp.watch(jsFiles, ['js']);
    gulp.watch(lessFiles, ['less']);
    gulp.watch(cssFiles, ['css']);
});

gulp.task('default', ['js', 'less', 'css', 'watch']);