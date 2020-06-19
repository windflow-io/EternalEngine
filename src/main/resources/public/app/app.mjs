import Vue from '/vendor/vue/vue.esm.browser.js';
import FlowApplication from '/components/FlowApplication.mjs'

new Vue({
    el: '#app',
    components: {FlowApplication},
    template:'<flow-application/>',
});
