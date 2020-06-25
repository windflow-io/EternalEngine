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
        pageLayout: null,
        pageComponents: [],
        pageData: {},
    },
    mutations: {
        setPageMeta(state, value) {
            state.pageMeta = value;
        },
        setPageLayout(state, value) {
            state.pageLayout = value;
        },
        setPageComponents(state, value) {
            state.pageComponents = value;
        },
        setPageData(state, value) {
            state.pageData = value;
        }

    },
    actions: {
        fetchPageData({context, commit, state}, payload) {
            return new Promise((resolve, reject) => {
                fetch('/api/pages/' + payload.host + payload.path)
                    .then((response) => response.json())
                    .then(data => {
                        commit('setPageMeta', data.metaData);
                        commit('setPageLayout', data.layout);
                        commit('setPageComponents', data.components);
                        commit('setPageData', data.data);
                        resolve(data);
                    });
            });
        }
    }
})

new Vue({
    el: '#app',
    store,
    components: {FlowApplication},
    template:'<flow-application/>',
});
