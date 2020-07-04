import {FlowArea} from '/modules/coreComponents.mjs'
export default {
    name: 'StackedLayout',
    components: {FlowArea},
    /**@TODO: Pull the themes from a Mixin from the Vue store **/
    template:
        `<div class="flex flex-row h-screen bg-cover bg-center items-center justify-center p-10" style="background-image: url('https://i.imgur.com/VJVmBUo.jpg');">
            <img src="https://i.imgur.com/yAxDnpu.png" class="relative z-10 max-w-4xl opacity-100">
        </div>                 
         `,

    data() {
        return {
            backgroundColor: Math.round(Math.random()) === 1 ? 'bg-gray-900' : 'bg-red-900',
            foregroundColor: Math.round(Math.random()) === 1 ? 'text-yellow-500' : 'text-green-500'
        }
    }
}
