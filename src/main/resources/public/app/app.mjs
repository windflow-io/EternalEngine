
import WindflowLayout from '/components/windflow-layout.mjs';
import Vue from '/vendor/vue/vue.esm.browser.js';

new Vue({
    el: '#app',
    beforeMount() {
        self = this;
        Vue.component("windflow-layout", function (resolve, reject) {
            fetch('/api/layouts/sidebar-layout.html')
                .then((response) => response.text())
                .then((layout) => {
                    WindflowLayout.template = layout;
                    self.doResolve(resolve);

                });
            fetch('/api/pages/localhost:8080')
                .then((response) => response.json())
                .then((page) => {
                    WindflowLayout.data = function() {
                        return {
                            page: page,
                            display: {}
                        }
                    }
                    self.doResolve(resolve);
                });
        });
    },
    methods: {
        doResolve(resolve) {
            if (WindflowLayout.template && WindflowLayout.data) {
                resolve(WindflowLayout);
            }
        }
    }
});
