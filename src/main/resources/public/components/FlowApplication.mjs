import Vue from '/vendor/vue/vue.esm.browser.js';

export default {
    name: 'FlowApplication',
    template: `
        <div>
            {{domain}} - {{path}}
            <component :is="layoutComponent"/>
            Which is: {{layoutComponent}}
        </div>
     `,
    data() {
        return {
            layoutComponent: null
        }
    },
    beforeMount() {
        import('/api/components/left-menu-layout.mjs').then((module)=>{
            Vue.component('LeftMenuLayout', module.default);
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
