const express = require('express');
const router = express.Router();
const fs = require('fs');
const path = require('path');

/* GET home page. */
router.get('/', (req, res, next) => {
    res.redirect(req.app.locals.site.contextPath + '/usou/index');
});

router.get('/index', (req, res, next) => {
    res.render('usou/index', {
        page: {
            title: 'platform'
        }
    });
});

router.get('/demo', (req, res, next) => {
    res.render('usou/demo', {
        page: {
            title: 'platform'
        }
    });
});

router.get('/search', (req, res, next) => {
    res.render('usou/search', {
        page: {
            title: 'platform'
        }
    });
});

router.get('/clusterList', (req, res, next) => {
    res.render('usou/clusterList', {
        page: {
            title: 'cluster'
        }
    });
});

router.get('/clusterDetail', (req, res, next) => {
    res.render('usou/clusterDetail', {
        page: {
            title: 'clusterDetail',
            id: req.query.id,
            name: req.query.name
        }
    });
});

router.get('/nodeList', (req, res, next) => {
    res.render('usou/nodeList', {
        page: {
            title: 'nodeList',
            id: req.query.id,
            name: req.query.name
        }
    });
});

router.get('/nodeDetail', (req, res, next) => {
    res.render('usou/nodeDetail', {
        page: {
            title: 'nodeDetail',
            id: req.query.id,
            nodeId: req.query.nodeId,
            name: req.query.name
        }
    });
});

router.get('/searchData_Browse', (req, res, next) => {
    res.render('usou/searchData_Browse', {
        page: {
            title: 'searchData_Browse',
            id: req.query.id,
            name: req.query.name
        }
    });
});

router.get('/searchData_Basic', (req, res, next) => {
    res.render('usou/searchData_Basic', {
        page: {
            title: 'searchData_Basic',
            id: req.query.id,
            name: req.query.name
        }
    });
});

router.get('/searchData_Complex', (req, res, next) => {
    res.render('usou/searchData_Complex', {
        page: {
            title: 'searchData_Complex',
            id: req.query.id,
            name: req.query.name
        }
    });
});

router.get('/searchData_Sql', (req, res, next) => {
    res.render('usou/searchData_Sql', {
        page: {
            title: 'searchData_Sql',
            id: req.query.id,
            name: req.query.name
        }
    });
});

router.get('/participleManage_Dictionary', (req, res, next) => {
    res.render('usou/participleManage_Dictionary', {
        page: {
            title: 'participleManage_Dictionary',
            id: req.query.id,
            name: req.query.name,
            type: req.query.type,
            dic: req.query.dic
        }
    });
});

router.get('/dicManage', (req, res, next) => {
    res.render('usou/dicManage', {
        page: {
            title: 'dicManage',
            id: req.query.id,
            name: req.query.name
        }
    });
});

router.get('/participleManage_Test', (req, res, next) => {
    res.render('usou/participleManage_Test', {
        page: {
            title: 'participleManage_Test',
            id: req.query.id,
            name: req.query.name
        }
    });
});

router.get('/monitorManage_Result', (req, res, next) => {
    res.render('usou/monitorManage_Result', {
        page: {
            title: 'monitorManage_Result',
            name: req.query.name,
            id: req.query.id
        }
    });
});

router.get('/monitorManage_Rule', (req, res, next) => {
    res.render('usou/monitorManage_Rule', {
        page: {
            title: 'monitorManage_Rule',
            name: req.query.name,
            id: req.query.id
        }
    });
});

router.get('/resource/:fileName', function(req, res, next) {
    // 实现文件下载
    var fileName = req.params.fileName;

    var prePath = '';
    if (req.app.get('env') === 'production') prePath = 'build';

    var filePath = path.join(prePath, "public/usou/resources/", fileName);
    var stats = fs.statSync(filePath);
    if (stats.isFile()) {
        res.set({
            'Content-Type': 'application/octet-stream',
            'Content-Disposition': 'attachment; filename=' + fileName,
            'Content-Length': stats.size
        });
        fs.createReadStream(filePath).pipe(res);
    } else {
        res.end(404);
    }
});

router.get('/indexManage', (req, res, next) => {
    res.render('usou/indexManage', {
        page: {
            title: 'indexManage',
            id: req.query.id,
            name: req.query.name

        }
    });
});

router.get('/indicesDetail', (req, res, next) => {
    res.render('usou/indicesDetail', {
        page: {
            title: 'indicesDetail',
            name: req.query.name,
            id: req.query.id,
            indicesName: req.query.indicesName
        }
    });
});

router.get('/updateIndices', (req, res, next) => {
    res.render('usou/updateIndices', {
        page: {
            title: 'updateIndices',
            name: req.query.name,
            id: req.query.id
        }
    });
});
router.get('/securityManage_Role', (req, res, next) => {
    res.render('usou/securityManage_Role', {
        page: {
            title: 'securityManage_Role',
            name: req.query.name,
            id: req.query.id
        }
    });
});
router.get('/securityManage_User', (req, res, next) => {
    res.render('usou/securityManage_User', {
        page: {
            title: 'securityManage_User',
            name: req.query.name,
            id: req.query.id
        }
    });
});

module.exports = router;