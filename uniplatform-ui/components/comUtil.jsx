const util_formatTime = (ms) => {
    const ss = 1000;
    const mi = ss * 60;
    const hh = mi * 60;
    const dd = hh * 24;
    let day = ms / dd;
    return day < 0 ? "0 d" : day.toFixed(1) + "d";
};
const util_bytesToSize = (bytes) => {
    if (bytes == 0){
        return '0 B';
    }
    let k = 1024,
        sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
        i = Math.floor(Math.log(bytes) / Math.log(k));

    return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
};

const convertByteToGbUtil = (bytes) => {

  return bytes ?
    ( bytes / Math.pow(2, 30) ).toPrecision( 3 )
    :
    0;

};

const util_formatJson = (json, options) => {
        var reg = null,
                formatted = '',
                pad = 0,
                PADDING = '    ';
        options = options || {};
        options.newlineAfterColonIfBeforeBraceOrBracket = (options.newlineAfterColonIfBeforeBraceOrBracket === true) ? true : false;
        options.spaceAfterColon = (options.spaceAfterColon === false) ? false : true;
        if (typeof json !== 'string') {
            json = JSON.stringify(json);
        } else {
            json = JSON.parse(json);
            json = JSON.stringify(json);
        }
        reg = /([\{\}])/g;
    json = json.replace(reg, '\r\n$1\r\n');
        reg = /([\[\]])/g;
        json = json.replace(reg, '\r\n$1\r\n');
        reg = /(\,)/g;
    json = json.replace(reg, '$1\r\n');
        reg = /(\r\n\r\n)/g;
        json = json.replace(reg, '\r\n');
        reg = /\r\n\,/g;
        json = json.replace(reg, ',');
        if (!options.newlineAfterColonIfBeforeBraceOrBracket) {
            reg = /\:\r\n\{/g;
            json = json.replace(reg, ':{');
        reg = /\:\r\n\[/g;
            json = json.replace(reg, ':[');
        }
        if (options.spaceAfterColon) {
            reg = /\:/g;
            json = json.replace(reg, ':');
        }
        (json.split('\r\n')).forEach(function (node, index) {
                    var i = 0,
                        indent = 0,
                        padding = '';
                if (node.match(/\{$/) || node.match(/\[$/)) {
                        indent = 1;
                    } else if (node.match(/\}/) || node.match(/\]/)) {
                        if (pad !== 0) {
                        pad -= 1;
                        }
                    } else {
                        indent = 0;
                    }

                    for (i = 0; i < pad; i++) {
                        padding += PADDING;
                    }

                    formatted += padding + node + '\r\n';
                    pad += indent;
                }
        );
        return formatted;
};

const Timestamp = (ms) => {
    if(ms<10){
        return ms + 'ms';
    }else if (ms>10 && ms<1000*60){
        let s = ms/1000 + 's';
        return s;
    }else if(ms>1000*60){
        let unixTimestamp = new Date( ms/1000 ) ;
        Date.prototype.toLocaleString = function() {
            return (this.getHours()-8) + ":" + this.getMinutes() + ":" + this.getSeconds();
        };
        let commonTime = unixTimestamp.toLocaleString();
        return commonTime;
    }
}

export {util_formatTime, util_bytesToSize, convertByteToGbUtil, util_formatJson, Timestamp};
