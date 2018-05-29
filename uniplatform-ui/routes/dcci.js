/**
 * Created by lss on 2017/8/8.
 */
const express = require('express');
const router = express.Router();

router.get('/org', (req, res, next) => {
    res.redirect(req.app.locals.site.contextPath + '/dcci/org/index');
});

router.get('/org/index', (req, res, next) => {
    res.render('dcci/org/index', {
        page: {
            title: 'index'
        }
    });
});

router.get('/platform', (req, res, next) => {
    res.redirect(req.app.locals.site.contextPath + '/dcci/platform/index');
});

router.get('/platform/index', (req, res, next) => {
    res.render('dcci/platform/index', {
        page: {
            title: 'index'
        }
    });
});

router.get('/platform/site/list', (req, res, next) => {
    res.render('dcci/platform/site/list', {
        page: {
            title: 'list'
        }
    });
});

router.get('/platform/task/list', (req, res, next) => {
    res.render('dcci/platform/task/list', {
        page: {
            title: 'list'
        }
    });
});

router.get('/platform/require/sites', (req, res, next) => {
    res.render('dcci/platform/require/sites', {
        page: {
            title: 'sites',
            taskRelation: req.query.taskRelation
        }
    });
});

router.get('/platform/require/execution', (req, res, next) => {
    res.render('dcci/platform/require/execution', {
        page: {
            title: 'execution',
            id: req.query.id
        }
    });
});

router.get('/platform/require/upload', (req, res, next) => {
    res.render('dcci/platform/require/upload', {
        page: {
            title: 'upload'
        }
    });
});

router.get('/platform/machine/list', (req, res, next) => {
    res.render('dcci/platform/machine/list', {
        page: {
            title: 'list'
        }
    });
});

router.get('/platform/machine/users', (req, res, next) => {
    res.render('dcci/platform/machine/users', {
        page: {
            title: 'users',
            id: req.query.id
        }
    });
});

router.get('/platform/component/list', (req, res, next) => {
    res.render('dcci/platform/component/list', {
        page: {
            title: 'list',
            id: req.query.id
        }
    });
});

router.get('/platform/require/list', (req, res, next) => {
    res.render('dcci/platform/require/list', {
        page: {
            title: 'list',
            id: req.query.id
        }
    });
});

router.get('/platform/require/assign', (req, res, next) => {
    res.render('dcci/platform/require/assign', {
        page: {
            title: 'assign',
            id: req.query.id
        }
    });
});

router.get('/platform/task/sites', (req, res, next) => {
    res.render('dcci/platform/task/sites', {
        page: {
            title: 'sites',
            taskRelation: req.query.taskRelation
        }
    });
});

router.get('/platform/data/detail', (req, res, next) => {
    res.render('dcci/platform/data/detail', {
        page: {
            title: 'detail',
            apiId: req.query.apiId
        }
    });
});

router.get('/platform/data/list', (req, res, next) => {
    res.render('dcci/platform/data/list', {
        page: {
            title: 'list'
        }
    });
});

router.get('/platform/data/codes', (req, res, next) => {
    res.render('dcci/platform/data/codes', {
        page: {
            title: 'codes'
        }
    });
});

router.get('/platform/data/example', (req, res, next) => {
    res.render('dcci/platform/data/example', {
        page: {
            title: 'apiExample',
            id: req.query.id,
            apiId: req.query.apiId
        }
    });
});

router.get('/platform/data/add', (req, res, next) => {
    res.render('dcci/platform/data/add', {
        page: {
            title: 'add'
        }
    });
});

router.get('/platform/data/update', (req, res, next) => {
    res.render('dcci/platform/data/update', {
        page: {
            title: 'update',
            id: req.query.id
        }
    });
});

router.get('/org/data/list', (req, res, next) => {
    res.render('dcci/org/data/list', {
        page: {
            title: 'list'
        }
    });
});

router.get('/org/data/detail', (req, res, next) => {
    res.render('dcci/org/data/detail', {
        page: {
            title: 'detail',
            id: req.query.id
        }
    });
});

module.exports = router;