const fs = require("fs");
const path = require("path");

function loadFilms() {
  const csvFilePath = path.join(__dirname, "films.csv");
  const csvData = fs.readFileSync(csvFilePath, "utf8").split("\n");

  const films = csvData.map((line) => {
    const [title, synopsis, year, rating] = line.split(",");
    return { title, synopsis, year, rating };
  });

  return { film: films[Math.floor(Math.random() * films.length)] };
}

module.exports = { loadFilms };
