import Vue from '/vendor/vue/vue.esm.browser.js';
import VueX from '/vendor/vue/vuex.esm.browser.js'
import FlowApplication from '/components/FlowApplication.mjs'

Vue.use(VueX);

const store = new VueX.Store({
    state: {
        pageMeta: {
            title: null,
            description: null,
            httpStatus: null
        },
        pageComponents: []
    },
    mutations: {
        setPageMeta(state, value) {
            state.pageMeta = value;
        },
        setPageComponents(state, value) {
            state.pageComponents = value;
        }
    }
})

new Vue({
    el: '#app',
    store,
    components: {FlowApplication},
    template:'<flow-application/>',
});
