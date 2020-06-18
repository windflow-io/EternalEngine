import Vue from '/vendor/vue/vue.esm.browser.js';

new Vue({
    el: '#app',
    data: {
        message: 'ESM Browser Build!',
        component: null
    },
    components: {
    },
    async beforeMount() {

        const c = await import('/components/page.mjs')
        Vue.component("page", c.default);
        this.component = 'page';


        // fetch('/components/Page.vue')
        //     .then(response => response.text())
        //     .then((vueSource) => {
        //         const elem = document.createElement(null);
        //         elem.innerHTML = vueSource;
        //         const html = elem.getElementsByTagName("template")[0].innerHTML;
        //         const js = elem.getElementsByTagName("script")[0].innerText;
        //         const json = eval(js);
        //         console.log (json);
        //     });
    },
    methods: {
    }
});
