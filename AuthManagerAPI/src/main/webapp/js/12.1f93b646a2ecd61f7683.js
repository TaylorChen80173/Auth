webpackJsonp([12],{358:function(r,e,o){function t(r){o(431)}var n=o(1)(o(394),o(448),t,null,null);r.exports=n.exports},394:function(r,e,o){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var t=o(0);e.default={components:{QBtn:t.QBtn,QIcon:t.QIcon},data:function(){return{canGoBack:window.history.length>1}},methods:{goBack:function(){window.history.go(-1)}}}},421:function(r,e,o){e=r.exports=o(353)(!1),e.push([r.i,".error-page .error-code{height:50vh;width:100%;padding-top:15vh;color:hsla(0,0%,100%,.2);overflow:hidden}@media (orientation:landscape){.error-page .error-code{font-size:30vw}}@media (orientation:portrait){.error-page .error-code{font-size:30vh}}.error-page .error-card{border-radius:2px;margin-top:-50px;width:80vw;max-width:600px;padding:25px}.error-page .error-card>i{font-size:5rem}",""])},431:function(r,e,o){var t=o(421);"string"==typeof t&&(t=[[r.i,t,""]]),t.locals&&(r.exports=t.locals);o(354)("7b18c5ce",t,!0,{})},448:function(r,e){r.exports={render:function(){var r=this,e=r.$createElement,o=r._self._c||e;return o("div",{staticClass:"error-page window-height window-width bg-light column items-center no-wrap"},[o("div",{staticClass:"error-code bg-primary flex items-center content-center justify-center"},[r._v("\n    404\n  ")]),r._v(" "),o("div",[o("div",{staticClass:"error-card shadow-4 bg-white column items-center justify-center no-wrap"},[o("q-icon",{attrs:{name:"error_outline",color:"grey-5"}}),r._v(" "),o("p",{staticClass:"caption text-center"},[r._v("Oops. Nothing here...")]),r._v(" "),o("p",{staticClass:"text-center group"},[r.canGoBack?o("q-btn",{attrs:{color:"primary",push:"",icon:"keyboard_arrow_left"},on:{click:r.goBack}},[r._v("\n          Go back\n        ")]):r._e(),r._v(" "),o("q-btn",{attrs:{color:"primary",push:"","icon-right":"home"},on:{click:function(e){r.$router.replace("/")}}},[r._v("\n          Go home\n        ")])],1)],1)])])},staticRenderFns:[]}}});