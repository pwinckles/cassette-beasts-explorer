# Changelog

## [Unreleased] - ReleaseDate

## [1.0.2] - 2023-07-16

### Added

- Index bootleg species. Each bootleg is indexed as its own record with `bootleg` set to `true`.
- Index bootleg moves. Moves are associated to species bootlegs by the `compatible_bootleg` field. The values of
  this field are the species name followed by the bootleg type. For example, `Thwackalope Astral`.

### Fixed

- Corrected tenses in result count display
- Don't attempt to parse empty queries

## [1.0.1] - 2023-07-11

### Added

- Index species without `remaster_from` or `remaster_to` values as `none`
- Result count

## [1.0.0] - 2023-07-11

Initial release

[Unreleased]: https://github.com/pwinckles/cassette-beasts-explorer/compare/v1.0.2...HEAD
[1.0.2]: https://github.com/pwinckles/cassette-beasts-explorer/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/pwinckles/cassette-beasts-explorer/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/pwinckles/cassette-beasts-explorer/releases/tag/v1.0.0