import Vue from '/vendor/vue/vue.esm.browser.js';

export default {
    name: 'FlowApplication',
    template: `
        <div>
            <component :is="layoutComponent"/>
        </div>
    `,
    data() {
        return {
            layoutComponent: null
        }
    },
    beforeMount() {
        import('/api/components/LeftMenuLayout.mjs').then((module)=>{
            Vue.component(module.default.name, module.default);
            this.layoutComponent = module.default.name;
        });
    },
    computed: {
        'domain' : function () {
            return location.host
        },
        'path' : function() {
            return location.pathname
        }
    }
}
// https://vueschool.io/articles/vuejs-tutorials/enhance-router-to-work-with-spas/
// https://github.com/vitejs/vite
// https://github.com/vuejs/vitepress/blob/master/src/client/app/router.ts
