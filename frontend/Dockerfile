FROM node:16.14.2
USER 1000
WORKDIR /usr/src/app
COPY --chown=1000:1000 package*.json ./
RUN npm update --location=global npm && npm install
COPY --chown=1000:1000 . .
EXPOSE 3000
CMD ["npm", "run", "dev", "--", "--host"]
