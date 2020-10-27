export default {
    name: "NotFoundDisplay",
    props: ['errorTitle', 'errorDescription', 'errorDetail'],
    template:`
        <section class="text-center">
            <h1 class="text-6xl text-white">{{errorTitle}}</h1>
            <h2 class="text-3xl text-gray-400 -mt-4">{{errorDescription}}</h2>
            <h3 class="text-base text-gray-600 mt-2">{{errorDetail}}</h3>
        </section>
        `
}
