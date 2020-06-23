import Vue from '/vendor/vue/vue.esm.browser.js';
import VueX from '/vendor/vue/vuex.esm.browser.js'
import FlowApplication from '/api/components/FlowApplication.mjs'

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
    },
    actions: {
        fetchPageData(context, commit) {
            return new Promise((resolve, reject) => {fetch('/api/pages/' + document.location.href).then((response) => response.json()).then(data => resolve(data))})
        }
    }
})

new Vue({
    el: '#app',
    store,
    components: {FlowApplication},
    template:'<flow-application/>',
});
