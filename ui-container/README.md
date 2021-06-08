So a few things need to happen for the ui-container to work:

1. If you are following the instructions located here: https://docs.luigi-project.io/docs/application-setup?section=application-setup-for-react
   It will fail at around the devDependencies, that MIGHT be because the devDependencies
   are actually overriding the actual dependencies. I have, for the sake of this test,
   commented them out (babel-loader and webpack were a real pain in the butt)
2. The next thing that you want to fix is that you want to copy out the luigi-core and
   luigi-client libraries out into the public folder. This is for dev purposes, otherwise
   the React luigi wrapper does not work.