import { PrismaClient } from '@prisma/client';

const p = new PrismaClient();

async function main() {
    const r = await p.venue.findMany({ where: { name: { contains: 'itz', mode: 'insensitive' } }, select: { name: true, id: true } });
    console.log('Ritz search:', r);

    const r2 = await p.venue.findMany({ where: { name: { contains: 'Ritz', mode: 'insensitive' } }, select: { name: true, id: true } });
    console.log('Ritz exact:', r2);

    // Also search for it in Arabic
    const r3 = await p.venue.findMany({ where: { nameAr: { contains: 'ريتز', mode: 'insensitive' } }, select: { name: true, nameAr: true, id: true } });
    console.log('Ritz Arabic:', r3);
}

main()
    .catch(console.error)
    .finally(async () => {
        await p.$disconnect();
    });
