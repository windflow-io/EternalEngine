
import WindflowLayout from '/components/windflow-layout.mjs';
import Vue from '/vendor/vue/vue.esm.browser.js';

new Vue({
    el: '#app',
    beforeMount() {
        this.self = this;
        Vue.component("windflow-layout", function (resolve, reject) {
            fetch('/api/layouts/sidebar-layout.html')
                .then((response) => response.text())
                .then((layout) => {
                    WindflowLayout.template = layout;
                    resolve(WindflowLayout)
                });
        });
    }
});
