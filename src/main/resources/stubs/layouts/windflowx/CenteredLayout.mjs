import {FlowArea} from '/modules/coreComponents.mjs'
export default {
    name: 'CenteredLayout',
    components: {FlowArea},
    /**@TODO: Pull the themes from a Mixin from the Vue store **/
    template:
        `<div class="flex flex-row h-screen items-center justify-center p-10 bg-gray-900">
            <div class="relative z-10 max-w-4xl opacity-100"><div><flow-area name="center"></flow-area></div></div>
        </div>                 
         `,
}