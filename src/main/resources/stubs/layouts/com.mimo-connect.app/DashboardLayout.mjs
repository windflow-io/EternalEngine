import {FlowArea} from '/modules/coreComponents.mjs'
export default {
    name: 'DashboardLayout',
    components: {FlowArea},
    /**@TODO: Pull the themes from a Mixin from the Vue store **/
    template:
        `<section>
            <div class="w-full flex flex-row">
                <div class="flex-grow bg-red-100">1*</div>
                <div class="flex-grow bg-blue-100">2*</div>
                <div class="flex-grow bg-purple-100">3*</div>               
            </div>
            <div class=""></div>
        </section>                 
         `,
}
