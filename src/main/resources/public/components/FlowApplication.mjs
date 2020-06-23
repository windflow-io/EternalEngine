
import {loadComponent} from '/modules/windflowUtils.mjs';
export default {
    name: 'FlowApplication',
    template: `
        <div>
            {{domain}} - {{path}}
            <flow-layout/>
        </div>
     `,
    beforeMount() {
        loadComponent('FlowLayout', '/components/FlowLayout.mjs', '/api/layouts/sidebar-layout.html')
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
