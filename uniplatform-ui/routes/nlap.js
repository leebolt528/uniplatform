/**
 * Created by lss on 2017/8/8.
 */
const express = require('express');
const router = express.Router();
const request = require('request');
const ips = require( '../config/application.json' );
const url = 'http://' + ips.uniplatform.ip + ':' + ips.uniplatform.port + '/' + ips.uniplatform.name + '/nlap/admin/user/loginfo';

//const loginCheckout = require( './loginCheckout' );

const loginCheckout = (req, res, next) => {
    console.log(req.session)

    request(
        {
            method: 'POST',
            //url: 'http://172.16.3.116:9186/uniplatform/nlap/admin/user/loginfo',
            url: 'http://' + ips.uniplatform.ip + ':' + ips.uniplatform.port + '/' + ips.uniplatform.name + '/nlap/admin/user/loginfo'
        },
        (error, response, body) => {
            console.log(body);
            if (body && JSON.parse(body).code === 1) {
                next();
            } else {
                res.redirect(req.context.contextPath + '/nlap/index');
            }
        });
};

router.get('/', (req, res, next) => {
    res.redirect(req.app.locals.site.contextPath + '/nlap/index');
});

router.get('/index', (req, res, next) => {
    res.render('nlap/index', {
        page: {
            title: 'index'
        }
    });
});

router.get('/platform', loginCheckout, (req, res, next) => {
    console.log(req.session)
    res.redirect(req.app.locals.site.contextPath + '/nlap/platform/index');
});

router.get('/platform/index', (req, res, next) => {
    res.render('nlap/platform/index', {
        page: {
            title: 'platform'
        }
    });
});

router.get('/platform/dictionary',loginCheckout, (req, res, next) => {
    res.render('nlap/platform/dictionary', {
        page: {
            title: 'dictionary'
        }
    });
});

router.get('/platform/funcStrategy', (req, res, next) => {
    res.render('nlap/platform/funcStrategy', {
        page: {
            title: 'funcStrategy'
        }
    });
});

router.get('/platform/busiStrategy', (req, res, next) => {
    res.render('nlap/platform/busiStrategy', {
        page: {
            title: 'busiStrategy'
        }
    });
});

router.get('/platform/task', (req, res, next) => {
    res.render('nlap/platform/task', {
        page: {
            title: 'task'
        }
    });
});

router.get('/platform/model', (req, res, next) => {
    res.render('nlap/platform/model', {
        page: {
            title: 'model'
        }
    });
});

router.get('/platform/rules', (req, res, next) => {
    res.render('nlap/platform/rules', {
        page: {
            title: 'rules'
        }
    });
});

router.get('/platform/classManage', (req, res, next) => {
    res.render('nlap/platform/classManage', {
        page: {
            title: 'classManage'
        }
    });
});

router.get('/platform/labelManage', (req, res, next) => {
    res.render('nlap/platform/labelManage', {
        page: {
            title: 'labelManage'
        }
    });
});

router.get('/platform/corpusManage', (req, res, next) => {
    res.render('nlap/platform/corpusManage', {
        page: {
            title: 'corpusManage'
        }
    });
});

router.get('/platform/dataSet', (req, res, next) => {
    res.render('nlap/platform/dataSet', {
        page: {
            title: 'dataSet'
        }
    });
});

router.get('/product', (req, res, next) => {
    res.redirect(req.app.locals.site.contextPath + '/nlap/product/autoWordSegment');
});

router.get('/product/autoWordSegment', (req, res, next) => {
    res.render('nlap/product/autoWordSegment', {
        page: {
            title: 'autoWordSegment'
        }
    });
});

router.get('/product/abstractKeyword', (req, res, next) => {
    res.render('nlap/product/abstractKeyword', {
        page: {
            title: 'abstractKeyword'
        }
    });
});

router.get('/product/entityExtraction', (req, res, next) => {
    res.render('nlap/product/entityExtraction', {
        page: {
            title: 'entityExtraction'
        }
    });
});

router.get('/product/textComparison', (req, res, next) => {
    res.render('nlap/product/textComparison', {
        page: {
            title: 'textComparison'
        }
    });
});

router.get('/product/pinyinCallouts', (req, res, next) => {
    res.render('nlap/product/pinyinCallouts', {
        page: {
            title: 'pinyinCallouts'
        }
    });
});

router.get('/product/simpleConversion', (req, res, next) => {
    res.render('nlap/product/simpleConversion', {
        page: {
            title: 'simpleConversion'
        }
    });
});

router.get('/product/textSimilar', (req, res, next) => {
    res.render('nlap/product/textSimilar', {
        page: {
            title: 'textSimilar'
        }
    });
});

router.get('/product/eventExtraction', (req, res, next) => {
    res.render('nlap/product/eventExtraction', {
        page: {
            title: 'eventExtraction'
        }
    });
});

router.get('/product/emotionAnalysis', (req, res, next) => {
    res.render('nlap/product/emotionAnalysis', {
        page: {
            title: 'emotionAnalysis'
        }
    });
});

router.get('/product/themeRelated', (req, res, next) => {
    res.render('nlap/product/themeRelated', {
        page: {
            title: 'themeRelated'
        }
    });
});

router.get('/product/relationshipExtraction', (req, res, next) => {
    res.render('nlap/product/relationshipExtraction', {
        page: {
            title: 'relationshipExtraction'
        }
    });
});

router.get('/product/textCategorization', (req, res, next) => {
    res.render('nlap/product/textCategorization', {
        page: {
            title: 'textCategorization'
        }
    });
});

router.get('/product/userFile', (req, res, next) => {
    res.render('nlap/product/userFile', {
        page: {
            title: 'userFile'
        }
    });
});

router.get('/product/rulesClassification', (req, res, next) => {
    res.render('nlap/product/rulesClassification', {
        page: {
            title: 'rulesClassification'
        }
    });
});

router.get('/product/textClustering', (req, res, next) => {
    res.render('nlap/product/textClustering', {
        page: {
            title: 'textClustering'
        }
    });
});

router.get('/product/word2vec', (req, res, next) => {
    res.render('nlap/product/word2vec', {
        page: {
            title: 'word2vec'
        }
    });
});

router.get('/product/newWord', (req, res, next) => {
    res.render('nlap/product/newWord', {
        page: {
            title: 'newWord'
        }
    });
});

router.get('/product/addressMatch', (req, res, next) => {
    res.render('nlap/product/addressMatch', {
        page: {
            title: 'addressMatch'
        }
    });
});

router.get('/product/relatedWords', (req, res, next) => {
    res.render('nlap/product/relatedWords', {
        page: {
            title: 'relatedWords'
        }
    });
});

module.exports = router;