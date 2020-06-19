
import {loadComponent} from '/modules/windflowUtils.mjs';

export default {
    name: 'FlowApplication',
    template: '<flow-layout/>',
    beforeMount() {
        loadComponent('FlowLayout', '/components/FlowLayout.mjs', '/api/layouts/sidebar-layout.html')
    }
}

