var exec = require('cordova/exec');

var alipay = {
  payment: function(json, successFn, failureFn) {
    exec(successFn, failureFn, 'Alipay', 'payment', [json]);
  },
  payV2:function(json, successFn, failureFn) { 
    exec(successFn, failureFn, 'Alipay', 'payV2', [json]);
  }
}

module.exports = alipay;